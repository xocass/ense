package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.AuthRequest;
import gal.usc.etse.sharecloud.model.dto.LoginResponse;
import gal.usc.etse.sharecloud.model.dto.SessionTokens;
import gal.usc.etse.sharecloud.service.AuthService;

import gal.usc.etse.sharecloud.service.UserService;
/*import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
//@Tag(name = "Auth", description = "Endpoints de autenticación y gestión de usuarios")
public class AuthController {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "__Secure-RefreshToken";
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService,  UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }


    /*@Operation(
            summary = "Iniciar sesión",
            description = """
                Autentica al usuario y devuelve un accessToken JWT.
                El refresh token se envía como cookie HttpOnly.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })*/
    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
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
                .body(new LoginResponse(tokens.accessToken()));
    }


    /*@Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un usuario en el sistema con email y contraseña. Tras esto, debe completar la vinculación de Spotify."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })*/
    @PreAuthorize("isAnonymous()")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody AuthRequest req) {
        userService.register(req);

        // Devuelve 201 en caso de éxito
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /*@Operation(
            summary = "Refrescar token de acceso",
            description = """
                Genera un nuevo accessToken usando el refresh token presente en la cookie.
                El email debe enviarse como un string en el body.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })*/
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshCookie,
            @RequestBody String email
    ) {
        if (refreshCookie == null || email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("refresh: email recibido -- " + email);

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
                .body(new LoginResponse(result.accessToken()));
    }


    /*@Operation(summary = "Cerrar sesión", description = "Invalida el refresh token del usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sesión cerrada")
    })*/
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication auth) {
        String email = auth.getName();
        authService.invalidateTokens(email);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth/refresh")
                .maxAge(0) // borrar cookie
                .build();

        // En caso de exito: 204
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }



}
