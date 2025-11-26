package gal.usc.etse.sharecloud.gui_controller;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.clientModel.User;
import gal.usc.etse.sharecloud.http.SpotifyApi;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cSession {
    private FachadaGUI fgui;
    private User loggedUser;

    private SpotifyApi spotifyApi;


    public void initSpotifyApi(String email, String jwtToken) {spotifyApi = new SpotifyApi(email, jwtToken);}
    public void setFachadas(FachadaGUI fgui, User loggedUser){this.fgui=fgui; this.loggedUser=loggedUser;}

    @FXML
    public void clickLinkSpotify(){
        try {
            String url = spotifyApi.startSpotifyLink();

            // Crear nueva ventana con WebView
            Stage stage = new Stage();
            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();

            // LISTENER PARA DETECTAR CALLBACK
            engine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
                if (newLoc.startsWith("http://127.0.0.1:8080/api/user/spotify/callback")) {
                    try {
                        URI uri = new URI(newLoc);
                        String query = uri.getQuery();  // "code=xxx&state=yyy"
                        Map<String, String> params = parseQuery(query);

                        String code = params.get("code");
                        String state = params.get("state");

                        // Procesar tokens
                        spotifyApi.completeSpotifyLink(code, state);
                        stage.close();

                    } catch (Exception e) {e.printStackTrace();}
                }
            });

            engine.load(url);

            Scene scene = new Scene(webView, 500, 700);
            stage.setScene(scene);
            stage.setTitle("Conectar tu cuenta de Spotify");
            stage.show();
        } catch (Exception e) {e.printStackTrace();}
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;

        String[] params = query.split("&");
        for (String p : params) {
            String[] kv = p.split("=");
            if (kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }

    @FXML
    public void clickSearchFriends(){
        try {
            Map<String, Object> trackData = spotifyApi.getLastPlayedTrack();
            String formatted = spotifyApi.formatLastTrack(trackData);
            System.out.println("Última canción: " + formatted);
        } catch (Exception e) {e.printStackTrace();}

    }

    //CAMBIAR ESTO EN CUANTO PODAMOS NO GUARDAR TOKENS DE USUARIOS QUE NO SOMOS Y LA FECHA RECUPERARLA
    private User clickGetProfile() throws Exception {
        Map<String, Object> profile = spotifyApi.getProfile();

        List<Map<String, Object>> images = (List<Map<String, Object>>) profile.get("images");
        String imageUrl = new String("");
        if (images != null && !images.isEmpty()) {
            imageUrl = (String) images.getFirst().get("url");
        }

        return new User(
                (String)profile.get("email"),
                (String)profile.get("display_name"),
                null,
                (String)profile.get("country"),
                imageUrl,
                loggedUser.getAccessToken(),
                loggedUser.getRefreshToken()
                );
    }

    @FXML
    public void clickProfile() throws Exception{
        fgui.entrarPerfil(loggedUser,clickGetProfile());
    }

}
