package gal.usc.etse.sharecloud.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import gal.usc.etse.sharecloud.model.dto.LoginResponse;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {
    private static final CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .cookieHandler(cookieManager)
            .build();


    public static boolean refreshAccessToken(String email) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/auth/refresh"))
                    .POST(HttpRequest.BodyPublishers.ofString(email))
                    .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200 && res.body() != null) {
                ObjectMapper mapper = new ObjectMapper();
                LoginResponse dto = mapper.readValue(res.body(), LoginResponse.class);
                TokenManager.setAccessToken(dto.accessToken());
                return true;
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }


    public static HttpClient getClient() {
        return client;
    }

    public static CookieManager getCookieManager() {
        return cookieManager;
    }
}
