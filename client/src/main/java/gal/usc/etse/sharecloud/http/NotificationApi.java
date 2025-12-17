package gal.usc.etse.sharecloud.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gal.usc.etse.sharecloud.model.entity.Reaccion;

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
}
