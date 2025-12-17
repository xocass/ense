package gal.usc.etse.sharecloud.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gal.usc.etse.sharecloud.model.dto.LikeRequest;
import gal.usc.etse.sharecloud.model.entity.Reaccion;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class NotificationApi{
    private final static ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static List<Reaccion> getMyNotifications() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:8080/api/user/me/notifications"))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();

        HttpResponse<String> res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) throw new RuntimeException("HTTP " + res.statusCode());

        return mapper.readValue(res.body(), new TypeReference<List<Reaccion>>() {});
    }

    public static void likeTrack(String receiverId, String trackName) {

        LikeRequest dto = new LikeRequest(receiverId,trackName);

        HttpRequest req = null;
        try {
            req = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:8080/api/user/me/reactions/like"))
                    .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(dto)))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error mappeando like");
        }

        HttpResponse<String> res =
                null;
        try {
            res = ApiClient.getClient().send(req, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException("Error enviando like");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (res.statusCode() != 201 && res.statusCode() != 409) {
            throw new RuntimeException("Error liking track: HTTP " + res.statusCode());
        }
    }
}
