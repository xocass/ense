package gal.usc.etse.sharecloud.server.controller;

import gal.usc.etse.sharecloud.server.exception.DuplicateUserException;
import gal.usc.etse.sharecloud.server.model.dto.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface IAuthController {
    @PostMapping(
            path = "login",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> login(@RequestBody User user);

    @PostMapping(
            path = "register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<User> register(@RequestBody User user) throws DuplicateUserException;

    @PostMapping("refresh")
    ResponseEntity<Void> refresh(@CookieValue(name = "__Secure-RefreshToken") String refreshToken);

    @PostMapping("logout")
    ResponseEntity<Void> logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token);
}
