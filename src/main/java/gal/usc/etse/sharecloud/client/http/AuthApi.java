package gal.usc.etse.sharecloud.client.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import gal.usc.etse.sharecloud.client.clientModel.dto.AuthResponse;
import gal.usc.etse.sharecloud.client.clientModel.dto.UserAuthRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class AuthApi {
    private final ObjectMapper mapper = new ObjectMapper();

    private final HttpClient client = HttpClient.newHttpClient();
    private final String baseUrl = "https://localhost:8080/api/auth";


    public Integer register(String email, String password) throws Exception {
        UserAuthRequest req = new UserAuthRequest(email, password);
        String json = mapper.writeValueAsString(req);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode();
    }

    public AuthResponse login(String email, String password) throws Exception {
        UserAuthRequest req = new UserAuthRequest(email, password);
        String json = mapper.writeValueAsString(req);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Login failed: " + response.body());
        }

        return mapper.readValue(response.body(), AuthResponse.class);
    }
}
