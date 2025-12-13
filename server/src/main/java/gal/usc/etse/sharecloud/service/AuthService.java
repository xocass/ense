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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final KeyPair keyPair;
    private final StringRedisTemplate redis;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SpotifyActivityService spotifyActivityService;
    private final EmailService emailService;

    private final String SECRET = "__SECRET:_VERY_SECRETY_SECRET";
    @Value("${auth.jwt.ttl:PT15M}")
    private Duration accessTokenTTL;
    @Value("${auth.refresh.ttl:PT72H}")
    private Duration refreshTokenTTL;

    private static final Duration RESET_CODE_TTL = Duration.ofMinutes(10);



    @Autowired
    public AuthService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, KeyPair keyPair,
                       StringRedisTemplate redis, UserRepository userRepository, RoleRepository roleRepository,
                       SpotifyActivityService spotifyActivityService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.keyPair = keyPair;
        this.redis = redis;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.spotifyActivityService = spotifyActivityService;
        this.emailService = emailService;
    }

    public SessionTokens login(AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException(request.email()));

        String userId= user.getId();
        String accessToken = generateAccessToken(userId, user.getRoles());
        String refreshToken = UUID.randomUUID().toString();

        redis.opsForValue().set(
                "refresh:" + user.getEmail(),
                refreshToken,
                refreshTokenTTL
        );

        try {
            spotifyActivityService.updateListenedTrackState(userId, 10);
        }catch (Exception e){ System.err.println("Error Spotify - obtencion canciones: "+e.getMessage());}

        return new SessionTokens(userId, accessToken, refreshToken);
    }

    public void sendRecoveryCode(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return;
        }

        String code = generateSixDigitCode();
        redis.opsForValue().set(
                "reset:" + email,
                code,
                RESET_CODE_TTL
        );

        emailService.sendPasswordRecoveryEmail(email, code);
    }

    public void checkPasswordRecoveryCode(String email, String code) {
        String redisKey = "reset:" + email;
        String storedCode = redis.opsForValue().get(redisKey);

        if (storedCode == null || !storedCode.equals(code)) {
            throw new IllegalArgumentException("Invalid or expired recovery code");
        }
    }

    public void resetPassword(String email, String newPassword) {
        String redisKey = "reset:" + email;

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid recovery request"));
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        redis.delete(redisKey);
    }

    private String generateSixDigitCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }




    public String generateAccessToken(String userId, Set<String> roles) {
        return Jwts.builder()
                .subject(userId)
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

        String newAccessToken = generateAccessToken(user.getId(), user.getRoles());
        String newRefreshToken = UUID.randomUUID().toString();

        // Borrar viejo y guardar nuevo
        invalidateTokens(email);
        redis.opsForValue().set(
                "refresh:" + email,
                newRefreshToken,
                refreshTokenTTL
        );

        return new SessionTokens(user.getId(), newAccessToken, newRefreshToken);
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

            String userId = claims.getSubject();
            Set<String> roles = new HashSet<>(claims.get("roles", List.class));

            return new AuthenticatedUser(userId, roles);

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
