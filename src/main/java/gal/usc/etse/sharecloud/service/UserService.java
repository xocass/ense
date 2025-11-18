package gal.usc.etse.sharecloud.service;

import gal.usc.etse.sharecloud.exception.DuplicateUserException;
import gal.usc.etse.sharecloud.model.entity.Role;
import gal.usc.etse.sharecloud.model.dto.User;
import gal.usc.etse.sharecloud.repository.RoleRepository;
import gal.usc.etse.sharecloud.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
    public gal.usc.etse.sharecloud.model.entity.User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public List<User> get() {
        return userRepository.findAll().stream().map(User::from).toList();
    }
    public Page<User> get(PageRequest page) {
        return userRepository.findAll(page).map(User::from);
    }
    public User get(String email){
        return User.from(loadUserByUsername(email));
    }
    public User create(gal.usc.etse.sharecloud.model.dto.User userDto) throws DuplicateUserException {
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
        gal.usc.etse.sharecloud.model.entity.User entity =
                gal.usc.etse.sharecloud.model.entity.User.from(userDto, passwordEncoder)
                        .addRole(userRole);

        var saved = userRepository.save(entity);
        return gal.usc.etse.sharecloud.model.dto.User.from(saved);
    }

    public gal.usc.etse.sharecloud.model.dto.User update(gal.usc.etse.sharecloud.model.entity.User user) {
        var saved = userRepository.save(user);
        return gal.usc.etse.sharecloud.model.dto.User.from(saved);
    }


    // >>>>>>>>>>>>>    SPOTIFY
    public String startSpotifyLink(String email) throws Exception {
        gal.usc.etse.sharecloud.model.entity.User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);
        String state = UUID.randomUUID().toString();

        user.setSpotifyCodeVerifier(codeVerifier);
        user.setSpotifyState(state);
        userRepository.save(user);

        //Construir URL Spotify
        return SPOTIFY_AUTH_URL + "?client_id=" + clientId
                + "&response_type=code"
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&scope=" + URLEncoder.encode("user-read-private user-read-email", StandardCharsets.UTF_8)
                + "&state=" + state
                + "&code_challenge_method=S256"
                + "&code_challenge=" + codeChallenge;
    }

    // Completa el linking de Spotify
    public void completeSpotifyLink(String email, String code, String state) throws Exception {
        gal.usc.etse.sharecloud.model.entity.User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!state.equals(user.getSpotifyState())) {
            throw new RuntimeException("Invalid Spotify state");
        }

        // Intercambiar código por tokens
        Map<String, String> tokens = exchangeCodeForTokens(code, user.getSpotifyCodeVerifier());

        user.setSpotifyAccessToken(tokens.get("access_token"));
        user.setSpotifyRefreshToken(tokens.get("refresh_token"));
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
                .uri(URI.create("https://accounts.spotify.com/api/token"))
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
        tokens.put("expires_in", String.valueOf(json.getInt("expires_in"))); // opcional
        return tokens;
    }

    /*public void processSpotifyCallback(String code, String state) throws Exception {
        gal.usc.etse.sharecloud.model.entity.User user = userRepository.findBySpotifyState(state).orElseThrow(() -> new RuntimeException("Invalid state"));
        String codeVerifier = user.getSpotifyCodeVerifier();

        // TOKEN EXCHANGE
        HttpClient client = HttpClient.newHttpClient();

        String body = "client_id=" + clientId
                + "&grant_type=authorization_code"
                + "&code=" + code
                + "&redirect_uri=" + redirectUri
                + "&code_verifier=" + codeVerifier;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SPOTIFY_TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        user.setSpotifyAccessToken(json.getString("access_token"));
        user.setSpotifyRefreshToken(json.getString("refresh_token"));
        user.setSpotifyState(null);
        user.setSpotifyCodeVerifier(null);
        userRepository.save(user);
    }
    */

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


