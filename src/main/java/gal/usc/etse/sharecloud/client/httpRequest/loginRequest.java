package gal.usc.etse.sharecloud.client.httpRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gal.usc.etse.sharecloud.client.clientModel.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class loginRequest {


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
