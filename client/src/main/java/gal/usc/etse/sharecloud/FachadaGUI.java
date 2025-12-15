package gal.usc.etse.sharecloud;

/*
     ¡OLLO! Antes de nada:
             - Ter instalado jdk-21
             - Declarar en terminales a emplear:
                        $env:JAVA_HOME="C:\Program Files\Java\jdk-21"
                        $env:Path="$env:JAVA_HOME\bin;" + $env:Path
     Como runnear:
     1. Buildear desde root / do proxecto:          ./gradlew clean build
     2. Runnear en 1 terminal servidor primetro:    ./gradlew :server:bootRun
     3. Runnear en outra terminal cliente:          ./gradlew :client:run
 */

import gal.usc.etse.sharecloud.guiController.cLog;
import gal.usc.etse.sharecloud.guiController.cProfile;
import gal.usc.etse.sharecloud.guiController.cSession;
import gal.usc.etse.sharecloud.http.SpotifyApi;
import gal.usc.etse.sharecloud.http.TokenManager;
import gal.usc.etse.sharecloud.http.UserApi;
import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyArtist;
import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyTrack;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopArtistsResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopTracksResponse;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public class FachadaGUI extends Application {
    private Stage entrarStage;
    private static HostServices hostServices;


    public Stage getEntrarStage() {return entrarStage;}
    public void setEntrarStage(Stage entrarStage) {this.entrarStage = entrarStage;}

    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage){
        entrarStage=stage;
        entrarStage.setResizable(false);
        hostServices = getHostServices();
        iniciarSesion();
    }

    /* Función que muestra la pantalla de inicio de sesión */
    public void iniciarSesion() {
        System.out.println("\n\n>>>>>>>> ARRANCADO <<<<<<<<<\n\n");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/vLog.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 878, 422);
            cLog controller = fxmlLoader.getController();
            controller.setFachadas(this);

            entrarStage.setTitle("Iniciar sesión");
            entrarStage.setScene(scene);
            entrarStage.show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());}
    }

    public void entrarSesion(String email){

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/vPrincipal.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 820, 620);
            cSession controller = fxmlLoader.getController();
            controller.setEmail(email);
            controller.setFachadas(this);

            entrarStage.setTitle("Session");
            entrarStage.setScene(scene);
            entrarStage.show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());}
    }

    public void verCurrPerfil(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/vProfile.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(),1440,760);
            cProfile controller = fxmlLoader.getController();

            //CARGAR DATOS USUARIO
            SpotifyProfile profileView = SpotifyApi.getSpotifyProfile(TokenManager.getUserID());
            if(profileView.getImage()!=null) {
                Image pfp = new Image(profileView.getImage());
                controller.pfpView.setImage(pfp);
            }
            controller.usernameLabel.setText(profileView.getDisplayName());
            controller.countryLabel.setText(profileView.getCountry());
            controller.setFachadas(this,profileView);

            //CARGAR RECENTLY PLAYED
            SpotifyRecentlyPlayedResponse recentlyPlayed = SpotifyApi.getRecentlyPlayed(TokenManager.getUserID(),20,10);
            _cargarRecentlyPlayed(controller,recentlyPlayed);

            //CARGAR TOP TRACKS
            SpotifyTopTracksResponse topTracks = SpotifyApi.getTopTracks(TokenManager.getUserID(),10);
            _cargarTopTracks(controller,topTracks);

            //CARGAR TOP ARTISTAS
            SpotifyTopArtistsResponse topArtists = SpotifyApi.getTopArtists(TokenManager.getUserID(),10);
            _cargarTopArtistas(controller,topArtists);

            entrarStage.setTitle(profileView.getDisplayName());
            entrarStage.setScene(scene);
            entrarStage.show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());
        }catch(Exception e){System.err.println("Exception(getUser): "+e.getMessage());}
    }
    public void verOtroPerfil(String otherID){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/vProfile.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(),1440,760);
            cProfile controller = fxmlLoader.getController();

            //CARGAR DATOS USUARIO
            SpotifyProfile profileView = UserApi.getOtherSpotifyProfile(otherID);
            if(profileView.getImage()!=null) {
                Image pfp = new Image(profileView.getImage());
                controller.pfpView.setImage(pfp);
            }
            controller.usernameLabel.setText(profileView.getDisplayName());
            controller.countryLabel.setText(profileView.getCountry());
            controller.setFachadas(this,profileView);

            //CARGAR RECENTLY PLAYED
            SpotifyRecentlyPlayedResponse recentlyPlayed = UserApi.getOtherRecentlyPlayed(otherID);
            _cargarRecentlyPlayed(controller,recentlyPlayed);

            //CARGAR TOP TRACKS
            SpotifyTopTracksResponse topTracks = UserApi.getOtherTopTracks(otherID);
            _cargarTopTracks(controller,topTracks);

            //CARGAR TOP ARTISTAS
            SpotifyTopArtistsResponse topArtists = UserApi.getOtherTopArtists(otherID);
            _cargarTopArtistas(controller,topArtists);

            entrarStage.setTitle(profileView.getDisplayName());
            entrarStage.setScene(scene);
            entrarStage.show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());
        }catch(Exception e){System.err.println("Exception(getUser): "+e.getMessage());}
    }

    private void _cargarRecentlyPlayed(cProfile controller, SpotifyRecentlyPlayedResponse response){
        controller.recentlyPlayedBox.getChildren().clear();
        for (SpotifyRecentlyPlayedResponse.Item item : response.items()) {
                //cargar el template
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/gal/usc/etse/sharecloud/layouts/vTemplateRecentTrack.fxml")
                );
                Parent template;
                try {
                    template = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                gal.usc.etse.sharecloud.guiController.cTemplateRecentTrack cTemplate = loader.getController();

                //recuperar datos del response
                String trackName = item.track().name();

                String artists = item.track().artists()
                        .stream()
                        .map(SpotifyArtist::name)
                        .collect(Collectors.joining(", "));

                String imageUrl = null;
                if (item.track().album() != null
                        && item.track().album().images() != null
                        && !item.track().album().images().isEmpty()) {
                    imageUrl = item.track().album().images().getFirst().url();
                }

                Integer duration = item.track().duration_ms();
                //settear datos del template y añadir al hbox
                cTemplate.setName(trackName);
                cTemplate.setArtist(artists);
                if (imageUrl != null) cTemplate.setImage(new Image(imageUrl));
                cTemplate.setDuration(duration);

                controller.recentlyPlayedBox.getChildren().add(template);
        }
    }
    public void _cargarTopTracks(cProfile controller, SpotifyTopTracksResponse response) {
        controller.topTrackBox.getChildren().clear();
        for (SpotifyTrack track : response.items()) {
            //cargar el template
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/gal/usc/etse/sharecloud/layouts/vItemTemplate.fxml")
            );
            Parent template;
            try {
                template = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            gal.usc.etse.sharecloud.guiController.cItemTemplate cTemplate=loader.getController();

            //recuperar datos de cada
            String trackName = track.name();
            String artists = track.artists()
                    .stream()
                    .map(SpotifyArtist::name)
                    .collect(Collectors.joining(", "));

            String imageUrl = null;
            if (track.album() != null
                    && track.album().images() != null
                    && !track.album().images().isEmpty()) {
                imageUrl = track.album().images().getFirst().url();
            }

            //settear los datos al template y añadirlo al hbox
            cTemplate.setName(trackName);
            cTemplate.setArtist(artists);
            if (imageUrl != null) {
                cTemplate.setImage(new Image(imageUrl, true));
            }
            controller.topTrackBox.getChildren().add(template);
        }
    }
    public void _cargarTopArtistas(cProfile controller, SpotifyTopArtistsResponse response) {
        controller.topArtistBox.getChildren().clear();
        for (SpotifyTopArtistsResponse.Item artist : response.items()) {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/gal/usc/etse/sharecloud/layouts/vItemTemplate.fxml")
            );
            Parent template;
            try {
                template = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            gal.usc.etse.sharecloud.guiController.cItemTemplate cTemplate = loader.getController();

            //recuperar datos del response
            String artistName = artist.name();

            String imageUrl = null;
            if (artist.images() != null && !artist.images().isEmpty()) {
                imageUrl = artist.images().getFirst().url();
            }

            //settear los datos del template y añadirlo al hbox
            cTemplate.setName(artistName);
            cTemplate.invLabel();
            if (imageUrl != null) {
                cTemplate.setImage(new Image(imageUrl, true));
            }
            controller.topArtistBox.getChildren().add(template);
        }
    }
}



