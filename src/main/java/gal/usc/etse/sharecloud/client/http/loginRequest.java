package gal.usc.etse.sharecloud.client.http;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class loginRequest {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String baseUrl = "http://localhost:8080";

    public boolean authLogin(String email, String password) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String url = String.format(
                "http://localhost:8081/auth/login?email=%s&password=%s",
                URLEncoder.encode(email, StandardCharsets.UTF_8),
                URLEncoder.encode(password, StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200 && response.body().equals("OK");
    }
}
