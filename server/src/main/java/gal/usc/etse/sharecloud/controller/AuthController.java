package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.exception.DuplicateUserException;
import gal.usc.etse.sharecloud.model.dto.AuthResponse;
import gal.usc.etse.sharecloud.model.dto.UserAuth;
import gal.usc.etse.sharecloud.service.AuthService;
import gal.usc.etse.sharecloud.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "__Secure-RefreshToken";
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService users) {
        this.authService = authService;
        this.userService = users;
    }


    // >>>>>>>>>>    USUARIOS
    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserAuth user) {
        AuthResponse loggedUser = authService.login(user);

        return ResponseEntity.ok(loggedUser);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/register")
    public ResponseEntity<UserAuth> register(@RequestBody UserAuth user) throws DuplicateUserException {
        UserAuth createdUser = userService.create(user);

        return ResponseEntity.created(
                MvcUriComponentsBuilder.fromMethodName(UserController.class, "get",
                        user.email()).build().toUri())
                .body(createdUser);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.login(refreshToken));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        UserAuth user = authService.parseJWT(token.replaceFirst("^Bearer ", ""));
        authService.invalidateTokens(user.email());

        return ResponseEntity.noContent().build();
    }


    // >>>>>>>>>>    SPOTIFY
    /*@GetMapping("/callback")
    public ResponseEntity<String> spotifyCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            @RequestParam(value = "email", required = false) String email
    ) {
        try {
            if (email == null) {
                return ResponseEntity.badRequest()
                        .body("Missing email parameter for Spotify link");
            }

            userService.completeSpotifyLink(email, code, state);

            return ResponseEntity.ok("Spotify account linked successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error linking Spotify account: " + e.getMessage());
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<String> spotifyCallback(@RequestParam String code, @RequestParam String state) {
        try {
            userService.processSpotifyCallback(code, state);
            return ResponseEntity.ok("Spotify linked");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }*/


}
