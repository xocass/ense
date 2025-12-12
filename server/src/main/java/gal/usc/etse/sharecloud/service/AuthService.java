package gal.usc.etse.sharecloud.service;

import gal.usc.etse.sharecloud.model.dto.AuthRequest;
import gal.usc.etse.sharecloud.model.dto.AuthenticatedUser;
import gal.usc.etse.sharecloud.model.dto.SessionTokens;
import gal.usc.etse.sharecloud.model.entity.User;
import gal.usc.etse.sharecloud.repository.RoleRepository;
import gal.usc.etse.sharecloud.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final KeyPair keyPair;
    private final StringRedisTemplate redis;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final String SECRET = "__SECRET:_VERY_SECRETY_SECRET";
    @Value("${auth.jwt.ttl:PT15M}")
    private Duration accessTokenTTL;
    @Value("${auth.refresh.ttl:PT72H}")
    private Duration refreshTokenTTL;


    @Autowired
    public AuthService(AuthenticationManager authenticationManager, KeyPair keyPair, StringRedisTemplate redis,
                       UserRepository userRepository, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.keyPair = keyPair;
        this.redis = redis;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public SessionTokens login(AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(
                        request.email(),
                        request.password()
                )
        );

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

        String accessToken = generateAccessToken(email, user.getRoles());
        String refreshToken = UUID.randomUUID().toString();

        redis.opsForValue().set(
                "refresh:" + email,
                refreshToken,
                refreshTokenTTL
        );

        return new SessionTokens(accessToken, refreshToken);
    }


    public String generateAccessToken(String email, Set<String> roles) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(accessTokenTTL)))
                .notBefore(Date.from(Instant.now()))
                .claim("roles", roles)
                .signWith(keyPair.getPrivate())
                .compact();
    }

    public SessionTokens refreshAccessToken(String refreshToken, String email) {
        String storedToken = redis.opsForValue().get("refresh:" + email);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        String newAccessToken = generateAccessToken(email, user.getRoles());
        String newRefreshToken = UUID.randomUUID().toString();

        // Borrar viejo y guardar nuevo
        invalidateTokens(email);
        redis.opsForValue().set(
                "refresh:" + email,
                newRefreshToken,
                refreshTokenTTL
        );

        return new SessionTokens(newAccessToken, newRefreshToken);
    }

    public void invalidateTokens(String email) {
        redis.delete("refresh:" + email);
    }

    public AuthenticatedUser parseJWT(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();
            Set<String> roles = new HashSet<>(claims.get("roles", List.class));

            return new AuthenticatedUser(email, roles);

        } catch (Exception e) {
            throw new JwtException("Invalid token");
        }
    }
    public RoleHierarchyImpl loadRoleHierarchy() {
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
