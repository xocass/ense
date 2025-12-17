package gal.usc.etse.sharecloud.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import gal.usc.etse.sharecloud.model.dto.FriendRequest;
import gal.usc.etse.sharecloud.model.dto.UserSearchResult;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class FriendApi {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String BASE_URL = "http://127.0.0.1:8080/api/friend";



    public static List<UserSearchResult> getFriends() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/list?id=" + TokenManager.getUserID()))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> response =
                ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error buscando usuarios");
        }

        return Arrays.asList(
                mapper.readValue(response.body(), UserSearchResult[].class)
        );
    }

    public static List<FriendRequest> getPendingRequests() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/request/list/pending?id=" + TokenManager.getUserID()))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo solicitudes");
        }

        return Arrays.asList(
                mapper.readValue(response.body(), FriendRequest[].class)
        );
    }

    public static List<FriendRequest> getRequestVisibleNotifications() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/request/list/visible?id=" + TokenManager.getUserID()))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .GET()
                .build();
        HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo solicitudes");
        }

        return Arrays.asList(
                mapper.readValue(response.body(), FriendRequest[].class)
        );
    }

    public static void sendRequest(String receiverId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/request/send?senderId=" + TokenManager.getUserID() +"&receiverId="+ receiverId))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.discarding());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error procesando solicitud");
        }
    }

    public static void acceptRequest(String requestId) throws Exception {
        clientCall("/request/accept", requestId);
    }

    public static void rejectRequest(String requestId) throws Exception {
        clientCall("/request/reject", requestId);
    }

    private static void clientCall(String path, String requestId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path + "?requestId=" + requestId +
                        "&id=" + TokenManager.getUserID()))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .build();
        HttpResponse<Void> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.discarding());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error procesando solicitud");
        }
    }

    public static void sawFriendRequest(String requestId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/request/check?requestId=" + requestId))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .build();
        HttpResponse<Void> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.discarding());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error procesando solicitud");
        }
    }

    public static void deleteFriend(String friendId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/delete?id=" + TokenManager.getUserID() +"&targetId="+ friendId))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .DELETE().build();
        HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            throw new RuntimeException("Error eliminando amistad");
        }
    }

}
