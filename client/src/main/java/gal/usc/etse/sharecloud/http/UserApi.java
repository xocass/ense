package gal.usc.etse.sharecloud.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopArtistsResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopTracksResponse;
import gal.usc.etse.sharecloud.model.dto.UserSearchResult;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class UserApi {
    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();;
    private static final String BASE_URL = "http://127.0.0.1:8080/api/user";



    public static List<UserSearchResult> searchUsers(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/search/user?id=" + TokenManager.getUserID() + "&query=" + encodedQuery))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> response =
                ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error buscando usuarios");
        }

        return Arrays.asList(
                mapper.readValue(response.body(), UserSearchResult[].class)
        );
    }



    public static SpotifyProfile getOtherSpotifyProfile(String userID) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/"+userID+"/spotify/profile"))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo perfil Spotify: HTTP "+res.statusCode());
        }

        return mapper.readValue(res.body(), SpotifyProfile.class);
    }

    public static SpotifyRecentlyPlayedResponse getOtherRecentlyPlayed(String userID) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/"+userID+"/spotify/recently-played"))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo canciones reproducidas recientemente: HTTP " + res.statusCode());
        }

        return mapper.readValue(res.body(), SpotifyRecentlyPlayedResponse.class);
    }
    public static SpotifyTopTracksResponse getOtherTopTracks(String userID) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/"+userID+"/spotify/top-tracks"))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo canciones reproducidas recientemente: HTTP " + res.statusCode());
        }

        return mapper.readValue(res.body(), SpotifyTopTracksResponse.class);
    }
    public static SpotifyTopArtistsResponse getOtherTopArtists(String userID) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/"+userID+"/spotify/top-artists"))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo canciones reproducidas recientemente: HTTP " + res.statusCode());
        }

        return mapper.readValue(res.body(), SpotifyTopArtistsResponse.class);
    }

}
