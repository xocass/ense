package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.AuthRequest;
import gal.usc.etse.sharecloud.model.dto.LoginResponse;
import gal.usc.etse.sharecloud.model.dto.SessionTokens;
import gal.usc.etse.sharecloud.model.entity.User;
import gal.usc.etse.sharecloud.repository.UserRepository;
import gal.usc.etse.sharecloud.service.AuthService;
import gal.usc.etse.sharecloud.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Endpoints de autenticación y gestión de usuarios")
public class AuthController {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "__Secure-RefreshToken";
    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthService authService, UserService userService, UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.userRepository = userRepository;
    }



    @Operation(operationId = "login", summary = "Iniciar sesión",
            description = """
                Autentica al usuario y devuelve un accessToken JWT. El refresh token se envía como cookie HttpOnly.
                """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SessionTokens.class)
                    ),
                    links = {@Link(name = "Logout", operationId = "logout"),
                            @Link(name = "RefreshToken", operationId = "refreshToken")}
            ),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content)
    })
    @PreAuthorize("isAnonymous()")
    @PostMapping(path= "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody AuthRequest request) {
        SessionTokens tokens = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, tokens.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(30))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(tokens.accessToken(), tokens.userId()));
    }


    @Operation(operationId = "register", summary = "Registrar nuevo usuario",
            description = """
                Crea un usuario en el sistema con email y contraseña.
                Tras el registro, el usuario debe completar la vinculación de Spotify.
                """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado con éxito", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PreAuthorize("isAnonymous()")
    @PostMapping(
            path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> register(@RequestBody AuthRequest req) {
        userService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @Operation(operationId = "refreshToken", summary = "Refrescar token de acceso",
            description = """
                Genera un nuevo accessToken usando el refresh token presente en la cookie.
                El email debe enviarse como un string en el body.
                """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado correctamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginResponse.class)),
                    links = {@Link(name = "Logout", operationId = "logout")}
            ),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado", content = @Content)
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping(
            path = "/refresh",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshCookie,
            @RequestBody String email
    ) {
        if (refreshCookie == null || email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SessionTokens result = authService.refreshAccessToken(refreshCookie, email);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, result.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(30))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(result.accessToken(), result.userId()));
    }


    @Operation(operationId = "logout", summary = "Cerrar sesión",
            description = "Invalida el refresh token del usuario y elimina la cookie asociada")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sesión cerrada correctamente", content = @Content)
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication auth) {
        String userId = auth.getName();
        User user = userRepository.findById(userId).orElseThrow();

        authService.invalidateTokens(user.getEmail());

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth/refresh")
                .maxAge(0) // borrar cookie
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }


    @Operation(operationId = "forgotPassword", summary = "Solicitar código de recuperación de contraseña",
            description = """
                Genera un código de recuperación de 6 dígitos y lo envía por email.
                La respuesta es siempre 200 para evitar revelar si el email existe.
                """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proceso de recuperación iniciado", content = @Content)
    })
    @PostMapping(
            path = "/forgot-password",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> forgotPassword(@RequestBody String email) {
        email = email.trim();
        if (email.startsWith("\"") && email.endsWith("\"") && email.length() >= 2) {
            email = email.substring(1, email.length() - 1);
        }
        authService.sendRecoveryCode(email);
        return ResponseEntity.ok().build();
    }


    @Operation(operationId = "checkResetCode", summary = "Verificar código de recuperación",
            description = """
                Comprueba si el código de recuperación introducido es válido. No modifica la contraseña.
                """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Código válido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Código inválido o expirado", content = @Content)
    })
    @PostMapping(
            path = "/check-reset-code",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> checkResetCode(@RequestBody AuthRequest request) {
        authService.checkPasswordRecoveryCode(request.email(), request.password());
        return ResponseEntity.ok().build();
    }


    @Operation(operationId = "resetPassword", summary = "Restablecer contraseña",
            description = """
                Establece una nueva contraseña usando un código de recuperación válido.
                El código se invalida tras el uso.
                """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o código expirado", content = @Content)
    })
    @PatchMapping(
            path = "/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> resetPassword(@RequestBody AuthRequest request) {
        authService.resetPassword(request.email(), request.password());
        return ResponseEntity.ok().build();
    }

}
