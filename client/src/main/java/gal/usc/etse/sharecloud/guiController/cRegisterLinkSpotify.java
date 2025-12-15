package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.SpotifyApi;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class cRegisterLinkSpotify {
    private String email;
    private FachadaGUI fgui;
    @FXML private Button btnLinkSpotify;
    @FXML private Label statusLabel;


    public void setEmail(String email) {this.email=email;}
    public void setFachadas(FachadaGUI fgui) {this.fgui=fgui;}

    @FXML
    private void initialize() {statusLabel.setText("");}

    @FXML
    public void clickOnGoBack(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/popUpWarning.fxml")
            );
            Parent root = fxmlLoader.load();

            Stage popup = new Stage();
            popup.setScene(new Scene(root));
            popup.setTitle("Atención");
            popup.setResizable(false);

            cPopUpWarning controller = fxmlLoader.getController();
            controller.setFachadas(this.fgui);
            controller.getLabelWarning().setText("Atención");
            controller.getMessageWarming().setText("Es necesario tener vinculada una cuenta de Spotify para poder "+
                            "acceder a ShareCloud, ¿estás seguro que quieres volver al inicio y no finalizar el registro ahora?");

            // Hacerla modal
            popup.initOwner(fgui.getEntrarStage());
            popup.initModality(Modality.WINDOW_MODAL);

            popup.show();
        } catch (Exception e) {e.printStackTrace();}
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
                        fgui.registroCompletado();

                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            });
            engine.load(url);

            Scene scene = new Scene(webView, 500, 700);
            stage.setScene(scene);
            stage.setTitle("Conecta tu cuenta de Spotify");
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

}


