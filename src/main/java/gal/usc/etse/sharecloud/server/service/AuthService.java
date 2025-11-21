package gal.usc.etse.sharecloud.server.service;

import gal.usc.etse.sharecloud.server.exception.InvalidRefreshTokenException;
import gal.usc.etse.sharecloud.server.model.dto.AuthResponse;
import gal.usc.etse.sharecloud.server.model.dto.UserProfile;
import gal.usc.etse.sharecloud.server.model.entity.RefreshToken;
import gal.usc.etse.sharecloud.server.model.dto.UserAuth;
import gal.usc.etse.sharecloud.server.model.entity.Role;
import gal.usc.etse.sharecloud.server.model.entity.User;
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
import org.springframework.security.core.GrantedAuthority;
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

    public AuthResponse login(UserAuth user) {
        Authentication auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(user.email(), user.password()));

        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String accessToken = generateAccessToken(user.email(), roles);
        String refreshToken = regenerateRefreshToken(user);

        User entity = userRepository.findByEmail(user.email()).orElseThrow();

        return new AuthResponse(
                accessToken,
                refreshToken,
                UserProfile.from(entity)
        );
    }
    public AuthResponse login(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException(refreshToken));
        User user = userRepository.findByEmail(token.getUser())
                .orElseThrow(() -> new UsernameNotFoundException(token.getUser()));
        List<String> roles = user.getRoles().stream()
                .map(Role::getRolename)
                .toList();
        String accessToken = generateAccessToken(user.getEmail(), roles);
        String newRefreshToken = regenerateRefreshToken(UserAuth.from(user));

        return new AuthResponse(
                accessToken,
                newRefreshToken,
                UserProfile.from(user)
        );
    }


    // Métodos auxiliares

    private String generateAccessToken(String email, List<String> roles) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(tokenTTL)))
                .notBefore(Date.from(Instant.now()))
                .claim("roles", roles)
                .signWith(keyPair.getPrivate())
                .compact();
    }

    public String regenerateRefreshToken(UserAuth user) {
        UUID uuid = UUID.randomUUID();
        RefreshToken refreshToken = new RefreshToken(uuid.toString(), user.email(), refreshTTL.toSeconds());
        refreshTokenRepository.deleteAllByUser(user.email());
        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }

    public void invalidateTokens(String email) {
        refreshTokenRepository.deleteAllByUser(email);
    }

    public UserAuth parseJWT(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String email = claims.getSubject();
        var user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            return UserAuth.from(user.get());
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
