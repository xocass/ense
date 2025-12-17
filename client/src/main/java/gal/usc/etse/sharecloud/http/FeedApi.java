package gal.usc.etse.sharecloud.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gal.usc.etse.sharecloud.model.dto.FriendRequest;
import gal.usc.etse.sharecloud.model.dto.Like;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.entity.FeedItem;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class FeedApi {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String BASE_URL = "http://127.0.0.1:8080/api/user/me/feed";

    public static List<FeedItem> loadFeed(){
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL+"/listened-tracks?id="+TokenManager.getUserID()))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> res = null;
        try {
            res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (res.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo feed: HTTP " + res.statusCode() + " - " + res.body());
        }

        try {
            return  mapper.readValue(
                    res.body(),
                    new TypeReference<List<FeedItem>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void giveLike(String friendId, String trackName) throws Exception {
        String encodedQuery = URLEncoder.encode(trackName, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/like/give?id=" + TokenManager.getUserID() +"&friendId="+ friendId +"&trackName=" + encodedQuery))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.discarding());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error procesando solicitud");
        }
    }

    public static List<Like> getLikes() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/like/receive?id=" + TokenManager.getUserID()))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo solicitudes");
        }

        return Arrays.asList(
                mapper.readValue(response.body(), Like[].class)
        );
    }

    public static void deleteLike(String likeId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/like?id=" + likeId))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .DELETE().build();
        HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            throw new RuntimeException("Error eliminando like");
        }
    }
}
