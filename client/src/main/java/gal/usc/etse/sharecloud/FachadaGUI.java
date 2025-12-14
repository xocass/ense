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
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
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
            Scene scene = new Scene(fxmlLoader.load(), 820, 620);
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
            SpotifyRecentlyPlayedResponse recentlyPlayed = SpotifyApi.getRecentlyPlayed(TokenManager.getUserID(),10);
            _cargarRecentlyPlayed(controller,recentlyPlayed);

            entrarStage.setTitle(profileView.getDisplayName());
            entrarStage.setScene(scene);
            entrarStage.show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());
        }catch(Exception e){System.err.println("Exception(getUser): "+e.getMessage());}
    }


    private void _cargarRecentlyPlayed(cProfile controller, SpotifyRecentlyPlayedResponse response){
        controller.recentlyPlayedBox.getChildren().clear();
        int counter = 0;
        for (SpotifyRecentlyPlayedResponse.Item item : response.items()) {
        if(counter<5) {
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

            // Controller del template
            gal.usc.etse.sharecloud.guiController.cItemTemplate cTemplate = loader.getController();

        /* ==========================
           Datos desde el DTO
           ========================== */

            // Nombre de la canción
            String trackName = item.track().name();

            // Artistas (separados por coma)
            String artists = item.track().artists()
                    .stream()
                    .map(SpotifyRecentlyPlayedResponse.Artist::name)
                    .collect(Collectors.joining(", "));

            // Imagen del álbum (primer tamaño disponible)
            String imageUrl = null;
            if (item.track().album() != null
                    && item.track().album().images() != null
                    && !item.track().album().images().isEmpty()) {

                imageUrl = item.track().album().images().getFirst().url();
            }
            System.out.println("Foto n"+counter+": "+imageUrl);
        /* ==========================
           Inicialización del template
           ========================== */
            cTemplate.setName(trackName);
            cTemplate.setArtist(artists);
            if (imageUrl != null) cTemplate.setImage(new Image(imageUrl));

            controller.recentlyPlayedBox.getChildren().add(template);
            counter++;
        }
        }
    }
}


