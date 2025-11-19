package gal.usc.etse.sharecloud.server.controller;

import gal.usc.etse.sharecloud.server.exception.DuplicateUserException;
import gal.usc.etse.sharecloud.server.model.dto.User;
import gal.usc.etse.sharecloud.server.service.AuthService;
import gal.usc.etse.sharecloud.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.naming.AuthenticationException;
import java.time.Duration;


@RestController
@RequestMapping("/api/auth")
public class AuthController implements IAuthController {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "__Secure-RefreshToken";
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService users) {
        this.authService = authService;
        this.userService = users;
    }


    // >>>>>>>>>>    USUARIOS
    @Override
    //@PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody User user) {
        try {
            User loggedUser = authService.login(user);
        }catch (AuthenticationException e) {

        }
        String refreshToken = authService.regenerateRefreshToken(user);
        String refreshPath = MvcUriComponentsBuilder.fromMethodName(AuthController.class, "refresh", "").build().toUri().getPath();

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .secure(true)
                .httpOnly(true)
                .sameSite(Cookie.SameSite.STRICT.toString())
                .path(refreshPath)
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.noContent()
                .headers(headers -> headers.setBearerAuth(loggedUser.password()))
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @Override
    //@PreAuthorize("isAnonymous()")
    public ResponseEntity<User> register(@RequestBody User user) throws DuplicateUserException {
        User createdUser = userService.create(user);

        return ResponseEntity.created(MvcUriComponentsBuilder.fromMethodName(UserController.class, "get", user.email()).build().toUri())
                .body(createdUser);
    }

    @Override
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> refresh(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {
        User user = authService.login(refreshToken);

        return login(user);
    }

    @Override
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        User user = authService.parseJWT(token.replaceFirst("^Bearer ", ""));
        authService.invalidateTokens(user.email());
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, null).build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
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
