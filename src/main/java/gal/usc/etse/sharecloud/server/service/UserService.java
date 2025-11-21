package gal.usc.etse.sharecloud.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gal.usc.etse.sharecloud.server.exception.DuplicateUserException;
import gal.usc.etse.sharecloud.server.model.entity.Role;
import gal.usc.etse.sharecloud.server.model.dto.UserAuth;
import gal.usc.etse.sharecloud.server.model.entity.User;
import gal.usc.etse.sharecloud.server.repository.RoleRepository;
import gal.usc.etse.sharecloud.server.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private MongoTemplate mongoTemplate;

    // Atributos para Spotify
    private static final String SPOTIFY_AUTH_URL = "https://accounts.spotify.com/authorize";
    private static final String SPOTIFY_TOKEN_URL = "https://accounts.spotify.com/api/token";
    @Value("${spotify.clientId}")
    private String clientId;
    @Value("${spotify.clientSecret}")
    private String clientSecret;
    @Value("${spotify.redirectUri}")
    private String redirectUri;


    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // >>>>>>>>>>>>>    USUARIO
    @Override
    public gal.usc.etse.sharecloud.server.model.entity.User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public List<UserAuth> get() {
        return userRepository.findAll().stream().map(UserAuth::from).toList();
    }
    public Page<UserAuth> get(PageRequest page) {
        return userRepository.findAll(page).map(UserAuth::from);
    }
    public UserAuth get(String email){
        return UserAuth.from(loadUserByUsername(email));
    }
    public UserAuth create(UserAuth userDto) throws DuplicateUserException {
        if (userDto == null || userDto.email() == null || userDto.email().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (userDto.password() == null || userDto.password().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Comprobación de duplicados
        if (userRepository.existsByEmail(userDto.email())) {
            var existing = userRepository.findByEmail(userDto.email()).orElse(null);
            throw new DuplicateUserException(existing);
        }

        // Comprobación de que Role USER exista
        Role userRole = roleRepository.findByRolename("USER");
        if (userRole == null) {
            throw new IllegalStateException("Role USER does not exist in the database");
        }

        // Crear entidad correctamente
        User entity = User.from(userDto, passwordEncoder)
                        .addRole(userRole);
        var saved = userRepository.save(entity);
        return UserAuth.from(saved);
    }


    // >>>>>>>>>>>>>    SPOTIFY
    public String startSpotifyLink(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);
        String state = UUID.randomUUID().toString();

        user.setSpotifyCodeVerifier(codeVerifier);
        user.setSpotifyState(state);
        userRepository.save(user);

        String scope = "user-read-private user-read-email user-read-recently-played";

        return SPOTIFY_AUTH_URL + "?"
                + "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8)
                + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8)
                + "&code_challenge_method=S256"
                + "&code_challenge=" + URLEncoder.encode(codeChallenge, StandardCharsets.UTF_8);
    }
    // Completa el linking de Spotify
    @Transactional
    public void completeSpotifyLink(String code, String state) throws Exception {
        User user = userRepository.findBySpotifyState(state).orElseThrow(() -> new RuntimeException("No user found for state"));

        if (!Objects.equals(state, user.getSpotifyState())) {
            throw new RuntimeException("Invalid Spotify state");
        }
        // Intercambiar código por tokens
        Map<String, String> tokens = exchangeCodeForTokens(code, user.getSpotifyCodeVerifier());

        user.setSpotifyAccessToken(tokens.get("access_token"));
        user.setSpotifyRefreshToken(tokens.get("refresh_token"));

        // Calcular expiry
        if (tokens.containsKey("expires_in")) {
            long expiresIn = Long.parseLong(tokens.get("expires_in"));
            long expiresAt = Instant.now().getEpochSecond() + expiresIn;
            user.setSpotifyAccessTokenExpiresAt(expiresAt);
        } else {
            user.setSpotifyAccessTokenExpiresAt(null);
        }

        // Limpiar campos temporales
        user.setSpotifyCodeVerifier(null);
        user.setSpotifyState(null);

        userRepository.save(user);
    }

    private Map<String, String> exchangeCodeForTokens(String code, String codeVerifier) throws Exception {
        HttpClient http = HttpClient.newHttpClient();
        String form = new StringBuilder()
                .append("grant_type=authorization_code")
                .append("&code=").append(URLEncoder.encode(code, StandardCharsets.UTF_8))
                .append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8))
                .append("&client_id=").append(URLEncoder.encode(clientId, StandardCharsets.UTF_8))
                .append("&client_secret=").append(URLEncoder.encode(clientSecret, StandardCharsets.UTF_8))
                .append("&code_verifier=").append(URLEncoder.encode(codeVerifier, StandardCharsets.UTF_8))
                .toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SPOTIFY_TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error exchanging code for tokens: " + response.body());
        }

        JSONObject json = new JSONObject(response.body());
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", json.getString("access_token"));
        tokens.put("refresh_token", json.getString("refresh_token"));
        Object exp = json.get("expires_in");
        if (exp != null) tokens.put("expires_in", String.valueOf(exp));

        return tokens;
    }

    // Comprueba si el token expiró
    public boolean tokenExpired(User user) {
        Long exp = user.getSpotifyAccessTokenExpiresAt();
        if (exp == null) return true;
        return Instant.now().getEpochSecond() > exp;
    }

    // Refrescar access token usando refresh_token
    @Transactional
    public void refreshSpotifyToken(User user) throws Exception {
        if (user.getSpotifyRefreshToken() == null) throw new RuntimeException("No refresh token available");

        HttpClient http = HttpClient.newHttpClient();
        String form = new StringBuilder()
                .append("grant_type=refresh_token")
                .append("&refresh_token=").append(URLEncoder.encode(user.getSpotifyRefreshToken(), StandardCharsets.UTF_8))
                .append("&client_id=").append(URLEncoder.encode(clientId, StandardCharsets.UTF_8))
                .append("&client_secret=").append(URLEncoder.encode(clientSecret, StandardCharsets.UTF_8))
                .toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SPOTIFY_TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error refreshing token: " + response.body());
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = mapper.readValue(response.body(), Map.class);

        String newAccess = (String) json.get("access_token");
        Object exp = json.get("expires_in");
        // refresh token puede o no venir en respuesta
        String newRefresh = (String) json.get("refresh_token");

        user.setSpotifyAccessToken(newAccess);
        if (newRefresh != null) user.setSpotifyRefreshToken(newRefresh);

        if (exp != null) {
            long expiresIn = Long.parseLong(String.valueOf(exp));
            long expiresAt = Instant.now().getEpochSecond() + expiresIn;
            user.setSpotifyAccessTokenExpiresAt(expiresAt);
        }

        userRepository.save(user);
    }

    // Obtener ultima cancion escuchada
    public Map<String, Object> getLastPlayedTrack(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getSpotifyAccessToken() == null) throw new IllegalStateException("Usuario sin Spotify vinculado.");

        try {
            if (tokenExpired(user)) {
                refreshSpotifyToken(user);
                // reload user from DB in case save changed it:
                user = userRepository.findByEmail(email).orElseThrow();
            }

            String url = "https://api.spotify.com/v1/me/player/recently-played?limit=1";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + user.getSpotifyAccessToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Spotify error: " + response.body());
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), Map.class);

        } catch (Exception e) {
            throw new RuntimeException("Error llamando a Spotify", e);
        }
    }

    // Obtener perfil del usuario en Spotify
    public Map<String, Object> getSpotifyProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getSpotifyAccessToken() == null) throw new IllegalStateException("Usuario sin Spotify vinculado.");

        try {
            if (tokenExpired(user)) {
                refreshSpotifyToken(user);
                user = userRepository.findByEmail(email).orElseThrow();
            }

            String url = "https://api.spotify.com/v1/me";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + user.getSpotifyAccessToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Spotify error: " + response.body());
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), Map.class);

        } catch (Exception e) {
            throw new RuntimeException("Error llamando a Spotify", e);
        }
    }

    // >>>>>>>>>>>    MÉTODOS AUXILIARES PKCE OAuth 2.0
    private String generateCodeVerifier() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String generateCodeChallenge(String codeVerifier) throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hashed = sha256.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
    }


    /*public void linkSpotify(String email, String clientId, String clientSecret, String redirectUri, String authCode) throws Exception {
        var user = loadUserByUsername(email);

        // Construir la petición para obtener tokens de Spotify
        HttpClient client = HttpClient.newHttpClient();
        String body = "grant_type=authorization_code" +
                "&code=" + URLEncoder.encode(authCode, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException("No se pudo vincular Spotify: " + response.body());
        }

        // Parsear JSON de la respuesta
        JSONObject json = new JSONObject(response.body());
        String accessToken = json.getString("access_token");
        String refreshToken = json.getString("refresh_token");

        // Guardar en la base de datos del usuario
        user.setSpotifyId(clientId);
        user.setSpotifySecret(clientSecret);
        user.setSpotifyRedirectUri(redirectUri);
        user.setSpotifyAccessToken(accessToken);
        user.setSpotifyRefreshToken(refreshToken);

        userRepository.save(user);
    }

     */
}


