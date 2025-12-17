package gal.usc.etse.sharecloud.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.entity.FeedItem;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
}
