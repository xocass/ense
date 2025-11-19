package gal.usc.etse.sharecloud.server.service;

import gal.usc.etse.sharecloud.server.exception.InvalidRefreshTokenException;
import gal.usc.etse.sharecloud.server.model.entity.RefreshToken;
import gal.usc.etse.sharecloud.server.model.dto.User;
import gal.usc.etse.sharecloud.server.repository.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import io.jsonwebtoken.Jwts;
import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final KeyPair keyPair;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${auth.jwt.ttl:PT15M}")
    private Duration tokenTTL;
    @Value("${auth.refresh.ttl:PT72H}")
    private Duration refreshTTL;


    @Autowired
    public AuthService(
            AuthenticationManager authenticationManager,
            KeyPair keyPair,
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.keyPair = keyPair;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public User login(User user) throws AuthenticationException {
        Authentication auth = authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(user.email(), user.password()));

        List<String> roles = auth.getAuthorities()
                .stream()
                .filter(authority -> authority instanceof SimpleGrantedAuthority)
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = Jwts.builder()
                .subject(auth.getName())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(tokenTTL)))
                .notBefore(Date.from(Instant.now()))
                .claim("roles", roles)
                .signWith(keyPair.getPrivate())
                .compact();

        return new User(user.email(), token, new HashSet<>(roles));
    }

    public User login(String refreshToken) throws AuthenticationException {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);

        if (token.isPresent()) {
            var user = userRepository.findByEmail(token.get().getUser()).orElseThrow(() -> new UsernameNotFoundException(token.get().getUser()));

            return login(User.from(user));
        }

        throw new InvalidRefreshTokenException(refreshToken);
    }

    public String regenerateRefreshToken(User user) {
        UUID uuid = UUID.randomUUID();
        RefreshToken refreshToken = new RefreshToken(uuid.toString(), user.email(), refreshTTL.toSeconds());
        refreshTokenRepository.deleteAllByUser(user.email());
        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }

    public void invalidateTokens(String email) {
        refreshTokenRepository.deleteAllByUser(email);
    }

    public User parseJWT(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String email = claims.getSubject();
        var user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            return User.from(user.get());
        } else {
            throw new UsernameNotFoundException("User (email) not found");
        }
    }

    public RoleHierarchy loadRoleHierarchy() {
        RoleHierarchyImpl.Builder builder = RoleHierarchyImpl.withRolePrefix("");

        roleRepository.findAll().forEach(role -> {
            if (!role.getIncludes().isEmpty()) {
                builder.role("ROLE_"+role.getRolename()).implies(
                        role.getIncludes().stream().map(i -> "ROLE_"+i.getRolename()).toArray(String[]::new)
                );
            }
            if (!role.getPermissions().isEmpty()) {
                builder.role("ROLE_"+role.getRolename()).implies(
                        role.getPermissions().stream().map(p -> p.getResource()+":"+p.getAction()).toArray(String[]::new)
                );
            }
        });

        return builder.build();
    }

}
