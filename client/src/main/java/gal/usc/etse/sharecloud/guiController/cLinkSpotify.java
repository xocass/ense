package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.http.SpotifyApi;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class cLinkSpotify {
    private String email;
    private cRegister cRegister;
    @FXML private Button btnLinkSpotify;

    public void setEmail(String email) {this.email=email;}
    public void setFachadas(cRegister cRegister) {this.cRegister=cRegister;}

    @FXML
    private void initialize() {}

    @FXML
    public void clickLeave(){

    }

    @FXML
    public void clickOnLinkSpotify() {
        try {
            String url = SpotifyApi.startSpotifyLink(email);

            // Crear nueva ventana con WebView
            Stage stage = new Stage();
            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();

            // Listener para detectar CALLBACK de Spotify
            engine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
                System.out.println("Navigating: " + newLoc);

                if (newLoc.startsWith("http://127.0.0.1:8080/api/spotify/callback")) {
                    try {
                        URI uri = new URI(newLoc);
                        String query = uri.getQuery();  // "code=xxx&state=yyy"
                        Map<String, String> params = parseQuery(query);

                        String code = params.get("code");
                        String state = params.get("state"); // es el email

                        // Procesar tokens en backend
                        SpotifyApi.completeSpotifyLink(code, state);

                        stage.close();
                        closePopup();
                        cRegister.closeWindow();
                        cRegister.returnToLogin();

                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            });
            engine.load(url);

            Scene scene = new Scene(webView, 500, 700);
            stage.setScene(scene);
            stage.setTitle("Conectar tu cuenta de Spotify");
            stage.show();

        } catch (Exception ex) {ex.printStackTrace();}
    }

    private Map<String, String> parseQuery(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                        arr -> URLDecoder.decode(arr[0], StandardCharsets.UTF_8),
                        arr -> URLDecoder.decode(arr[1], StandardCharsets.UTF_8)
                ));
    }


    public void closePopup() {
        ((Stage) btnLinkSpotify.getScene().getWindow()).close();
    }
}


