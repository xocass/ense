package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.exception.DuplicateUserException;
import gal.usc.etse.sharecloud.exception.InvalidRefreshTokenException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@RestControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {


    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResponse handle(BadCredentialsException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        error.setDetail("Provided credentials do not match any registered user");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "bad-credentials").build().toUri());
        error.setTitle(ex.getMessage());

        return ErrorResponse.builder(ex, error).build();
    }
    @ExceptionHandler(JwtException.class)
    public ErrorResponse handle(JwtException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        error.setDetail(ex.getMessage());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "expired-token").build().toUri());
        error.setTitle("The token has expired");

        return ErrorResponse.builder(ex, error).build();
    }
    @ExceptionHandler(DuplicateUserException.class)
    public ErrorResponse handle(DuplicateUserException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        error.setDetail("Email "+ex.getUser().getEmail()+" already exists in the database with the following data: "+ex.getUser().toString());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "duplicated-user").build().toUri());
        error.setTitle("User already exists!");

        return ErrorResponse.builder(ex, error).build();
    }
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ErrorResponse handle(InvalidRefreshTokenException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        error.setDetail("Refresh token "+ex.getToken()+" is invalid");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "refresh-token-user").build().toUri());
        error.setTitle("Invalid refresh token!");

        return ErrorResponse.builder(ex, error).build();
    }


}
