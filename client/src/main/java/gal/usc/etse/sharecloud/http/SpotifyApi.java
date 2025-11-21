package gal.usc.etse.sharecloud.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpotifyApi {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String email;
    private final String jwtToken;

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl = "http://127.0.0.1:8080/api/user";

    public SpotifyApi(String email, String jwtToken){this.email=email;this.jwtToken=jwtToken;}


    // Genera URL para iniciar el linking de Spotify
    public String startSpotifyLink() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/spotify/link?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)))
                .header("Authorization", "Bearer " + jwtToken)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo URL de Spotify: " + response.body());
        }
        return response.body();
    }

    // Completa el linking de Spotify con code y state de callback
    public void completeSpotifyLink(String code, String state) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/spotify/callback?code=" + code + "&state=" + state))
                .header("Authorization", "Bearer " + jwtToken)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Error vinculando Spotify: " + response.body());
        }
    }

    // Obtiene perfil de Spotify del usuario
    public Map<String, Object> getProfile() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/" + URLEncoder.encode(email, StandardCharsets.UTF_8) + "/spotify/profile"))
                .header("Authorization", "Bearer " + jwtToken)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo perfil Spotify: " + response.body());
        }

        return mapper.readValue(response.body(), Map.class);
    }

    // Obtiene última canción reproducida
    public Map<String, Object> getLastPlayedTrack() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/" + URLEncoder.encode(email, StandardCharsets.UTF_8) + "/spotify/last-track"))
                .header("Authorization", "Bearer " + jwtToken)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo última canción: " + response.body());
        }

        return mapper.readValue(response.body(), Map.class);
    }

    // Extrae información legible de la última canción
    public String formatLastTrack(Map<String, Object> trackData) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) trackData.get("items");
        if (items != null && !items.isEmpty()) {
            Map<String, Object> track = (Map<String, Object>) items.get(0).get("track");
            String trackName = (String) track.get("name");
            List<Map<String, Object>> artists = (List<Map<String, Object>>) track.get("artists");
            String artistNames = artists.stream().map(a -> (String) a.get("name")).collect(Collectors.joining(", "));
            return trackName + " — " + artistNames;
        }
        return "No hay canciones recientes";
    }
}
