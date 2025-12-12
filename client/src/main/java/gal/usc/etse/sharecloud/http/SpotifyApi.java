package gal.usc.etse.sharecloud.http;


import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;


public class SpotifyApi {
    private final static ObjectMapper mapper = new ObjectMapper();
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
            throw new RuntimeException("Error linking Spotify account");
        }
    }

    public static SpotifyProfile getSpotifyProfile(String userID) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/" + userID + "/spotify/me"))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo perfil Spotify");
        }

        return mapper.readValue(res.body(), SpotifyProfile.class);

    }
}
