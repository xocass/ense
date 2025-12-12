package gal.usc.etse.sharecloud.filter;

import com.mongodb.lang.NonNull;
import gal.usc.etse.sharecloud.model.dto.AuthenticatedUser;
import gal.usc.etse.sharecloud.service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final AuthService authenticationService;

    @Autowired
    public JwtFilter(AuthService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException, JwtException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(token == null || !token.startsWith("Bearer ")){
            chain.doFilter(request, response);
            return;
        }

        String jwt = token.substring(7);
        AuthenticatedUser user = authenticationService.parseJWT(jwt);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        user.userId(),
                        null,
                        user.roles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}
