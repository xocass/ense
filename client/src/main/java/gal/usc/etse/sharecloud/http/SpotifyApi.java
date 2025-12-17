package gal.usc.etse.sharecloud.http;


import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopArtistsResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopTracksResponse;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;


public class SpotifyApi {
    private final static ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final static String serverSpotifyUrl = "http://127.0.0.1:8080/api/spotify";

    public static String startSpotifyLink(String email) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(serverSpotifyUrl + "/start-link?email=" + email))
                .GET()
                .build();
        HttpResponse<String> res =
                ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() == 200) {
            return res.body();
        }

        throw new RuntimeException("Error obtaining Spotify link URL");
    }

    public static void completeSpotifyLink(String code, String state) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(serverSpotifyUrl + "/complete-link"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                                + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8)
                ))
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        System.out.println("STATUS = " + res.statusCode());
        System.out.println("BODY = " + res.body());
        if (res.statusCode() != 200) {
            throw new RuntimeException("Error linking Spotify account: HTTP "+res.statusCode());
        }
    }

    public static SpotifyProfile getSpotifyProfile(String userID) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/me/spotify/profile?id="+userID))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo perfil Spotify: HTTP "+res.statusCode());
        }

        return mapper.readValue(res.body(), SpotifyProfile.class);
    }

    public static SpotifyRecentlyPlayedResponse getRecentlyPlayed(String userID, int limitReturn, int limitSave) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/me/spotify/recently-played?id="+userID+"&limitSave=" + limitSave+"&limitReturn=" + limitReturn))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo canciones reproducidas recientemente: HTTP " + res.statusCode());
        }

        return mapper.readValue(res.body(), SpotifyRecentlyPlayedResponse.class);
    }

    public static SpotifyTopTracksResponse getTopTracks(String userID, int limit) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/me/spotify/top-tracks?id="+userID+"&limit=" + limit))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo canciones reproducidas recientemente: HTTP " + res.statusCode());
        }
        return mapper.readValue(res.body(), SpotifyTopTracksResponse.class);
    }

    public static SpotifyTopArtistsResponse getTopArtists(String userID, int limit) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/me/spotify/top-artists?id="+userID+"&limit=" + limit))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo canciones reproducidas recientemente: HTTP " + res.statusCode());
        }
        return mapper.readValue(res.body(), SpotifyTopArtistsResponse.class);
    }

    public static boolean isFollowing(String id, String currID) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/me/spotify/following/"+id+"?currID="+currID))
                .header("Authorization","Bearer "+TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());


        if (res.statusCode() != 200) {
            throw new Exception("Error verificando si sigue a usuario: HTTP " + res.statusCode());
        }
        return (res.body().trim().equals("[true]"));
    }

    public static void doFollow(String targetSpotifyID, String currID) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/me/spotify/do-follow/"+targetSpotifyID+"?currID="+currID))
                .header("Authorization","Bearer "+TokenManager.getAccessToken())
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 204) {
            throw new Exception("Error verificando si sigue a usuario: HTTP " + res.statusCode());
        }
    }

    public static void doUnfollow(String targetSpotifyID, String currID) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/me/spotify/do-unfollow/"+targetSpotifyID+"?currID="+currID))
                .header("Authorization","Bearer "+TokenManager.getAccessToken())
                .DELETE()
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 204) {
            throw new Exception("Error verificando si sigue a usuario: HTTP " + res.statusCode());
        }
    }
}
