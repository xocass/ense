package gal.usc.etse.sharecloud.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import gal.usc.etse.sharecloud.model.dto.AuthRequest;
import gal.usc.etse.sharecloud.model.dto.LoginResponse;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthApi {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String BASE_URL = "http://127.0.0.1:8080/api/auth";


    public static int login(String email, String password) throws Exception {
        AuthRequest req= new AuthRequest(email, password);
        String json = mapper.writeValueAsString(req);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                ApiClient.getClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            LoginResponse dto = mapper.readValue(response.body(), LoginResponse.class);
            TokenManager.setAccessToken(dto.accessToken());
            TokenManager.setUserID(dto.userID());
        }

        return response.statusCode();
    }

    public static int register(String email, String password) throws Exception {
        AuthRequest req = new AuthRequest(email, password);
        String json = mapper.writeValueAsString(req);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<Void> response =
                ApiClient.getClient().send(httpRequest, HttpResponse.BodyHandlers.discarding());

        return response.statusCode();
    }

    public static void logout(String email) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/auth/logout"))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response =
                ApiClient.getClient().send(req, HttpResponse.BodyHandlers.discarding());

        if(response.statusCode() == 401) {
            if (ApiClient.refreshAccessToken(email)) {
                // retry
                HttpRequest retry = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/auth/logout"))
                        .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();
                ApiClient.getClient().send(retry, HttpResponse.BodyHandlers.discarding());

            } else {
                // refresh falló
                TokenManager.reset();
                ApiClient.getCookieManager().getCookieStore().removeAll();
                throw new RuntimeException("La sesión ha expirado. Debes iniciar sesión.");
            }
        }

        TokenManager.reset();
        ApiClient.getCookieManager().getCookieStore().removeAll();
    }
}
