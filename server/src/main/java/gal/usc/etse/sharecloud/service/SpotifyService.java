package gal.usc.etse.sharecloud.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gal.usc.etse.sharecloud.model.dto.*;
import gal.usc.etse.sharecloud.model.entity.FriendRequestStatus;
import gal.usc.etse.sharecloud.model.entity.User;
import gal.usc.etse.sharecloud.repository.FriendRequestRepository;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;

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

    public final static String SPOTIFY_API_URI = "https://api.spotify.com/v1";
    private final UserRepository userRepo;
    private final FriendService friendService;
    private final FriendRequestRepository friendRequestRepository;



    @Autowired
    public SpotifyService(UserRepository userRepo, FriendService friendService,  FriendRequestRepository friendRequestRepository) {
        this.userRepo = userRepo;
        this.friendService = friendService;
        this.friendRequestRepository = friendRequestRepository;
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



    public SpotifyProfile getSpotifyUserProfile(String userId) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new UsernameNotFoundException(userId));
        if (user.getSpotifyAccessTokenExpiresAt().isBefore(Instant.now())) {
            refreshSpotifyAccessToken(user);
        }

        String spotifyAccessToken = user.getSpotifyAccessToken();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SPOTIFY_API_URI + "/me"))
                .header("Authorization", "Bearer " + spotifyAccessToken)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            String URI = SPOTIFY_API_URI + "/me";
            response = retryGETRequest(user, URI);
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error retrieving Spotify profile: " + response.body());
        }

        return mapJsonToSpotifyProfile(response);
    }


    public SpotifyTopArtistsResponse getTopArtists(String userId, int limit) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new UsernameNotFoundException(userId));
        if (user.getSpotifyAccessTokenExpiresAt().isBefore(Instant.now())) {
            refreshSpotifyAccessToken(user);
        }
        String spotifyAccessToken = user.getSpotifyAccessToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SPOTIFY_API_URI + "/me/top/artists?limit=" + limit))
                .header("Authorization", "Bearer " + spotifyAccessToken)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            String URI = SPOTIFY_API_URI + "/me/top/artists?limit=" + limit;
            response = retryGETRequest(user, URI);
        }
        if (response.statusCode() != 200) {throw new RuntimeException("Error retrieving top artists");}

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.body(), SpotifyTopArtistsResponse.class);
    }

    public SpotifyTopTracksResponse getTopTracks(String userId, int limit) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new UsernameNotFoundException(userId));
        if (user.getSpotifyAccessTokenExpiresAt().isBefore(Instant.now())) {
            refreshSpotifyAccessToken(user);
        }
        String spotifyAccessToken = user.getSpotifyAccessToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SPOTIFY_API_URI + "/me/top/tracks?limit=" + limit))
                .header("Authorization", "Bearer " + spotifyAccessToken)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            String URI = SPOTIFY_API_URI + "/me/top/tracks?limit=" + limit;
            response = retryGETRequest(user, URI);
        }
        if (response.statusCode() != 200) {throw new RuntimeException("Error retrieving top tracks");}

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.body(), SpotifyTopTracksResponse.class);
    }

    public SpotifyRecentlyPlayedResponse getRecentlyPlayed(String userId, int limit) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new UsernameNotFoundException(userId));

        if (user.getSpotifyAccessTokenExpiresAt().isBefore(Instant.now())) {
            refreshSpotifyAccessToken(user);
        }
        String spotifyAccessToken = user.getSpotifyAccessToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SPOTIFY_API_URI + "/me/player/recently-played?limit=" + limit))
                .header("Authorization", "Bearer " + spotifyAccessToken)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            String URI = SPOTIFY_API_URI + "/me/player/recently-played?limit=" + limit;
            response= retryGETRequest(user, URI);
        }
        if (response.statusCode() != 200) {throw new RuntimeException("Error fetching recently played");}

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper.readValue(response.body(), SpotifyRecentlyPlayedResponse.class);
    }

    public UserBooleans getBooleansUser(String currentUserId, String targetId) throws Exception {
        System.out.println("targetId: " + targetId + ", currid: "+ currentUserId);
        User user = userRepo.findById(currentUserId).orElseThrow(() -> new UsernameNotFoundException(currentUserId));

        if (user.getSpotifyAccessTokenExpiresAt().isBefore(Instant.now())) {
            refreshSpotifyAccessToken(user);
        }
        // IS-FOLLOWING
        User target = userRepo.findById(targetId).orElseThrow(() -> new UsernameNotFoundException(targetId));
        String spotifyID=target.getSpotifyProfile().getSpotifyID();;

        String uri = SPOTIFY_API_URI
                + "/me/following/contains"
                + "?type=user"
                + "&ids=" + spotifyID;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Bearer " + user.getSpotifyAccessToken())
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Retry en 401 (igual que el resto del servicio)
        if (response.statusCode() == 401) {
            response = retryGETRequest(user, uri);
        }
        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Error checking Spotify following status: " + response.body()
            );
        }
        Boolean isFollowing = response.body().contains("true");

        // IS-FRIEND
        List<UserSearchResult> friends = friendService.getFriends(user.getId());
        boolean isFriend = friends.stream()
                .anyMatch(friend -> friend.id().equals(targetId));

        // IS-PENDING
        boolean isPending= friendRequestRepository
                .existsBySenderIdAndReceiverIdAndStatusOrSenderIdAndReceiverIdAndStatus(currentUserId, targetId, FriendRequestStatus.PENDING,
                        targetId, currentUserId, FriendRequestStatus.PENDING);

        return new UserBooleans(targetId, isFriend, isFollowing, isPending);
    }

    public void doFollow(String targetSpotifyID, String currID) throws Exception {

        User user = userRepo.findById(currID)
                .orElseThrow(() -> new UsernameNotFoundException(currID));

        if (user.getSpotifyAccessTokenExpiresAt().isBefore(Instant.now())) {
            refreshSpotifyAccessToken(user);
        }

        String uri = SPOTIFY_API_URI
                + "/me/following"
                + "?type=user"
                + "&ids=" + targetSpotifyID;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Bearer " + user.getSpotifyAccessToken())
                .PUT(HttpRequest.BodyPublishers.noBody()) // 🔑 AQUÍ
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 204) {
            return; // OK
        }

        throw new RuntimeException(
                "Error following Spotify user. HTTP "
                        + response.statusCode()
                        + " - " + response.body()
        );
    }

    public void doUnfollow(String targetSpotifyID, String currID) throws Exception {
        User user = userRepo.findById(currID)
                .orElseThrow(() -> new UsernameNotFoundException(currID));

        if (user.getSpotifyAccessTokenExpiresAt().isBefore(Instant.now())) {
            refreshSpotifyAccessToken(user);
        }


        String uri = SPOTIFY_API_URI
                + "/me/following"
                + "?type=user"
                + "&ids=" + targetSpotifyID;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Bearer " + user.getSpotifyAccessToken())
                .DELETE()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 204)throw new RuntimeException(
                "Error unfollowing response: " + response.body()
        );
    }

    public SpotifyRecentlyPlayedResponse getPlayedToday(String userId) throws Exception {
        long after = midnightEpochMillis();
        User user = userRepo.findById(userId).orElseThrow(() -> new UsernameNotFoundException(userId));

        if (user.getSpotifyAccessTokenExpiresAt().isBefore(Instant.now())) {
            refreshSpotifyAccessToken(user);
        }
        String spotifyAccessToken = user.getSpotifyAccessToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SPOTIFY_API_URI + "/me/player/recently-played?after=" + after))
                .header("Authorization", "Bearer " + spotifyAccessToken)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            String URI = SPOTIFY_API_URI + "/me/player/recently-played?after=" + after;
            response= retryGETRequest(user, URI);
        }
        if (response.statusCode() != 200) {throw new RuntimeException(
                "Spotify error fetching recently played: HTTP "
                        + response.statusCode() + " - " + response.body()
        );}

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper.readValue(response.body(), SpotifyRecentlyPlayedResponse.class);
    }

    public long midnightEpochMillis() {
        return LocalDate.now(ZoneId.systemDefault())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }


    private HttpResponse<String> retryGETRequest(User user, String redirectUri) throws Exception {
        refreshSpotifyAccessToken(user);
        String spotifyAccessToken = user.getSpotifyAccessToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(redirectUri))
                .header("Authorization", "Bearer " + spotifyAccessToken)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    private SpotifyProfile mapJsonToSpotifyProfile(HttpResponse<String> response) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        String id = json.get("id").asText();
        String displayName = json.path("display_name").asText(null);
        String emailResp = json.path("email").asText(null);
        String country = json.path("country").asText(null);
        String profileURL=null;
        JsonNode externalURLs = json.path("external_urls");
        if(!externalURLs.isMissingNode()){
            profileURL=externalURLs.get("spotify").asText(null);
        }

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

}
