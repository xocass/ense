package gal.usc.etse.sharecloud.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gal.usc.etse.sharecloud.model.dto.SpotifyProfile;
import gal.usc.etse.sharecloud.model.dto.SpotifyTokenResponse;
import gal.usc.etse.sharecloud.model.entity.User;
import gal.usc.etse.sharecloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class SpotifyService {
    @Value("${spotify.clientId}")
    private String clientId;

    @Value("${spotify.clientSecret}")
    private String clientSecret;

    @Value("${spotify.redirectUri}")
    private String redirectUri;

    @Value("${spotify.scope}")
    private String scope;

    private final UserRepository userRepo;

    @Autowired
    public SpotifyService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public String generateLinkUrl(String email) {
        String encodedRedirect = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        String encodedScope = URLEncoder.encode(scope, StandardCharsets.UTF_8);

        return "https://accounts.spotify.com/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&scope=" + encodedScope
                + "&redirect_uri=" + encodedRedirect
                + "&state=" + email;
    }

    public SpotifyTokenResponse exchangeCodeForTokens(String code) throws IOException, InterruptedException {
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + encodedAuth)
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=authorization_code"
                                + "&code=" + code
                                + "&redirect_uri=" + redirectUri
                ))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to obtain Spotify tokens: " + response.body());
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.body(), SpotifyTokenResponse.class);
    }

    public User updateUserSpotifyTokens(String email, SpotifyTokenResponse tokens) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        user.setSpotifyAccessToken(tokens.accessToken());
        user.setSpotifyRefreshToken(tokens.refreshToken());
        user.setSpotifyAccessTokenExpiresAt(
                Instant.now().plusSeconds(tokens.expiresIn())
        );
        user.setSpotifyLinked(true);

        return user;
    }

    public void fetchSpotifyId(User user) throws Exception{
        String spotifyAccessToken = user.getSpotifyAccessToken();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/me"))
                .header("Authorization", "Bearer " + spotifyAccessToken)
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            // token inválido, refrescar
            refreshSpotifyAccessToken(user);
            spotifyAccessToken = user.getSpotifyAccessToken();
            request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/me"))
                    .header("Authorization", "Bearer " + spotifyAccessToken)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error retrieving Spotify profile: " + response.body());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        String id = json.get("id").asText();

        user.getSpotifyProfile().setSpotifyID(id);
        userRepo.save(user);
    }

    public SpotifyProfile getSpotifyUserProfile(String idRequest) throws Exception {
        User user = userRepo.findById(idRequest).orElseThrow(() -> new UsernameNotFoundException(idRequest));

        // Si el token caducó, refrescar
        if (user.getSpotifyAccessTokenExpiresAt().isBefore(Instant.now())) {
            refreshSpotifyAccessToken(user);
        }

        String spotifyAccessToken = user.getSpotifyAccessToken();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/me"))
                .header("Authorization", "Bearer " + spotifyAccessToken)
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            // token inválido, refrescar
            refreshSpotifyAccessToken(user);
            spotifyAccessToken = user.getSpotifyAccessToken();
            request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/me"))
                    .header("Authorization", "Bearer " + spotifyAccessToken)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error retrieving Spotify profile: " + response.body());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        String id = json.get("id").asText();
        String displayName = json.path("display_name").asText(null);
        String emailResp = json.path("email").asText(null);
        String country = json.path("country").asText(null);
        String profileURL = json.path("external-urls").asText(null);

        System.out.println("user {");
        System.out.println(id);
        System.out.println(displayName);
        System.out.println(emailResp);
        System.out.println(country);
        System.out.println(profileURL);
        System.out.println("}");
        // primera imagen (si existe)
        String image = null;
        JsonNode images = json.get("images");
        if (images != null && images.isArray() && images.size() > 0) {
            image = images.get(0).get("url").asText();
        }

        //recuperar número de seguidores
        Integer nFollowers = null;

        JsonNode followersNode = json.path("followers");
        if (!followersNode.isMissingNode()) {
            nFollowers = followersNode.path("total").isInt()
                    ? followersNode.get("total").asInt()
                    : null;
        }

        return new SpotifyProfile(
                id,
                displayName,
                emailResp,
                country,
                image,
                nFollowers,
                profileURL
        );
    }

    public void refreshSpotifyAccessToken(User user) throws Exception {
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=refresh_token&refresh_token=" + user.getSpotifyRefreshToken()
                ))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed refreshing Spotify token");
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        String newAccess = json.get("access_token").asText();
        long expiresIn = json.get("expires_in").asLong();

        user.setSpotifyAccessToken(newAccess);
        user.setSpotifyAccessTokenExpiresAt(Instant.now().plusSeconds(expiresIn));

        userRepo.save(user);
    }
}
