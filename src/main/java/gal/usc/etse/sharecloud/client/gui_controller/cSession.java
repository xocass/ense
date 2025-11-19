package gal.usc.etse.sharecloud.client.gui_controller;

import gal.usc.etse.sharecloud.client.FachadaGUI;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Component
public class cSession {
    private FachadaGUI fgui;
    private HostServices hostServices;
    private gal.usc.etse.sharecloud.client.model.dto.User loggedUser;

    public void setFachadas(FachadaGUI fgui, gal.usc.etse.sharecloud.client.model.dto.User loggedUser){this.fgui=fgui; this.loggedUser=loggedUser;}
    public void setHostServices(HostServices hostServices) {this.hostServices = hostServices;}
    public HostServices getHostServices(){return this.hostServices;}

    @FXML
    public void clickLinkSpotify(){
        try {
            String url = fgui.getUserService().startSpotifyLink(loggedUser.email());

            // Crear nueva ventana con WebView
            Stage stage = new Stage();
            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();

            // LISTENER PARA DETECTAR CALLBACK
            engine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
                if (newLoc.startsWith("http://127.0.0.1:8080/api/auth/callback")) {
                    try {
                        URI uri = new URI(newLoc);
                        String query = uri.getQuery();  // "code=xxx&state=yyy"

                        Map<String, String> params = parseQuery(query);

                        String code = params.get("code");
                        String state = params.get("state");

                        // Procesar tokens
                        fgui.getUserController().spotifyCallback(code, state, loggedUser.email());

                        // cerrar ventana WebView
                        stage.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            engine.load(url);

            Scene scene = new Scene(webView, 900, 700);
            stage.setScene(scene);
            stage.setTitle("Conectar tu cuenta de Spotify");
            stage.show();

            //fgui.getHostServices().showDocument(url);
        } catch (Exception e) {e.printStackTrace();}

    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        String[] params = query.split("&");

        for (String p : params) {
            String[] kv = p.split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    @FXML
    public void clickSearchFriends(){
        Map<String,Object> json = fgui.getUserController().getLastTrack(loggedUser.email()).getBody();

        Map<String, Object> item = ((List<Map<String, Object>>) json.get("items")).get(0);
        Map<String, Object> track = (Map<String, Object>) item.get("track");

        String name = (String) track.get("name");
        String artist = (String)
                ((Map<String, Object>) ((List<Object>) track.get("artists")).get(0)).get("name");

        System.out.println("Última canción: " + name + " — " + artist);

        String album = (String)
                ((Map<String, Object>) track.get("album")).get("name");

        /*String imageUrl = (String)
                ((Map<String, Object>) ((List<Object>)
                        ((Map<String, Object>) track.get("album")).get("images"))
                        .get(0)).get("url");*/
    }
}
