package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.ShareCloudBoot;
import gal.usc.etse.sharecloud.http.FriendApi;
import gal.usc.etse.sharecloud.http.SpotifyApi;
import gal.usc.etse.sharecloud.http.TokenManager;
import gal.usc.etse.sharecloud.http.UserApi;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopArtistsResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopTracksResponse;
import gal.usc.etse.sharecloud.model.dto.UserSearchResult;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;
import gal.usc.etse.sharecloud.model.entity.SpotifyResponseCompact;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class cMenu {

    private static int feedCounter=0;

    public static int getFeedCounter(){return feedCounter;}

    public static void setFeedCounter(int valor){feedCounter=valor;}

    public static void activarAmigos(Button btnFriends, Button btnSearch, ScrollPane friendsPane,
                                     VBox searchPane) {
        btnFriends.getStyleClass().setAll("pill-button-active");
        btnSearch.getStyleClass().setAll("pill-button");

        friendsPane.setVisible(true);
        friendsPane.setManaged(true);

        searchPane.setVisible(false);
        searchPane.setManaged(false);
    }

    public static void activarBuscar(Button btnFriends, Button btnSearch, ScrollPane friendsPane,
                                     VBox searchPane) {
        btnFriends.getStyleClass().setAll("pill-button");
        btnSearch.getStyleClass().setAll("pill-button-active");

        friendsPane.setVisible(false);
        friendsPane.setManaged(false);

        searchPane.setVisible(true);
        searchPane.setManaged(true);
    }

    public static void configurarBusqueda(TextField fieldSearch, VBox vboxResults, String userEmail){
        fieldSearch.setOnAction(e -> buscarUsuarios(vboxResults, fieldSearch, userEmail));
    }

    public static void configurarNotificaciones(ImageView btnNotification) {
        btnNotification.setOnMouseClicked(e -> abrirNotificaciones());
    }



    public static void cargarAmigos(VBox vboxFriends, String userEmail) {
        try {
            vboxFriends.getChildren().clear();

            List<UserSearchResult> amigos = FriendApi.getFriends();

            if (amigos.isEmpty()) {
                Label empty = new Label("Aún no tienes amigos añadidos");
                empty.getStyleClass().add("side-empty-text");
                vboxFriends.getChildren().add(empty);
                return;
            }

            for (UserSearchResult amigo : amigos) {
                Platform.runLater(()->{
                    vboxFriends.setFillWidth(true);
                    showResults(vboxFriends, amigo, true, userEmail);});
            }
        }catch (Exception e){e.printStackTrace();}
    }

    public static void buscarUsuarios(VBox vboxResults, TextField fieldSearch, String userEmail) {
        try {
            String query = fieldSearch.getText().trim();
            vboxResults.getChildren().clear();

            if (query.isEmpty()) return;

            List<UserSearchResult> resultados = UserApi.searchUsers(query);

            if (resultados.isEmpty()) {
                Label empty = new Label("No se encontraron usuarios con el nombre aportado");
                empty.getStyleClass().add("side-empty-text");
                vboxResults.getChildren().add(empty);
                return;
            }

            for (UserSearchResult other : resultados) {
                Platform.runLater(()->{
                    vboxResults.setFillWidth(true);
                    showResults(vboxResults, other, other.isFriend(), userEmail);});
            }
        } catch (Exception e) {e.printStackTrace();}

    }

    private static void showResults(VBox container, UserSearchResult user, boolean isFriend, String userEmail) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/templateUserItem.fxml")
            );
            HBox item = loader.load();

            cUserItem controller = loader.getController();
            controller.setOtherLabelName(user.username());
            controller.setOtherLabelCountry(user.country());
            controller.setOtherIsFriend(isFriend);
            controller.setOtherId(user.id());
            controller.setUserEmail(userEmail);
            if(user.image() != null){
                Image pic = new Image(user.image());
                controller.getOtherProfilePic().setImage(pic);
            }

            container.getChildren().add(item);
        } catch (IOException e) {e.printStackTrace();}
    }

    public static void abrirNotificaciones() {
        try {
                FXMLLoader fxmlLoader = new FXMLLoader(
                        ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/popUpNotification.fxml")
                );
                Parent root = fxmlLoader.load();

                Stage popup = new Stage();
                popup.setScene(new Scene(root));
                popup.setTitle("Notificaciones");
                popup.setResizable(false);

                cPopUpNotification controller = fxmlLoader.getController();
                FachadaGUI fgui=  FachadaGUI.getInstance();
                //controller.loadRequests();

                // Hacerla modal
                popup.initOwner(fgui.getEntrarStage());
                popup.initModality(Modality.WINDOW_MODAL);
                //popup.initStyle(StageStyle.TRANSPARENT);

                popup.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    public static void clickViewFeed(String userEmail){
        //Logica de obtener actividades de amigos
        //
         //

        FachadaGUI fgui = FachadaGUI.getInstance();
        fgui.irFeed(userEmail);
    }

    public static void clickOnUserProfile(String email) {
        Task<SpotifyResponseCompact> profileTask = new Task<>() {
            @Override
            protected SpotifyResponseCompact call() throws Exception {
                SpotifyProfile profileView = SpotifyApi.getSpotifyProfile(TokenManager.getUserID());
                SpotifyRecentlyPlayedResponse recentlyPlayed = SpotifyApi.getRecentlyPlayed(TokenManager.getUserID(), 20, 10);
                SpotifyTopTracksResponse topTracks = SpotifyApi.getTopTracks(TokenManager.getUserID(), 5);
                SpotifyTopArtistsResponse topArtists = SpotifyApi.getTopArtists(TokenManager.getUserID(), 5);

                SpotifyResponseCompact data = new SpotifyResponseCompact(profileView, recentlyPlayed, topTracks,
                        topArtists, null);
                return data;
            }
        };
        profileTask.setOnSucceeded(e -> {
            SpotifyResponseCompact data = profileTask.getValue();
            FachadaGUI fgui = FachadaGUI.getInstance();
            fgui.verCurrPerfil(data, email);
        });
        profileTask.setOnFailed(e -> {
            // openErrorPopUp()
;
            FachadaGUI fgui = FachadaGUI.getInstance();
            fgui.irFeed(email);
        });
            new Thread(profileTask).start();
    }

    public static void clickOnOtherProfile(String otherID, String userEmail) {
        Task<SpotifyResponseCompact> profileTask = new Task<>() {
            @Override
            protected SpotifyResponseCompact call() throws Exception {
                SpotifyProfile profileView = UserApi.getOtherSpotifyProfile(otherID);
                SpotifyRecentlyPlayedResponse recentlyPlayed = UserApi.getOtherRecentlyPlayed(otherID);
                SpotifyTopTracksResponse topTracks = UserApi.getOtherTopTracks(otherID);
                SpotifyTopArtistsResponse topArtists = UserApi.getOtherTopArtists(otherID);
                boolean isFollowing = SpotifyApi.isFollowing(TokenManager.getUserID(),otherID);

                SpotifyResponseCompact data = new SpotifyResponseCompact(profileView, recentlyPlayed, topTracks,
                        topArtists, isFollowing);
                return data;
            }
        };
        profileTask.setOnSucceeded(e -> {
            SpotifyResponseCompact data = profileTask.getValue();
            FachadaGUI fgui = FachadaGUI.getInstance();
            fgui.verOtroPerfil(data, userEmail);
        });
        profileTask.setOnFailed(e -> {
            // openErrorPopUp()

            FachadaGUI fgui = FachadaGUI.getInstance();
            fgui.irFeed(userEmail);
        });
        new Thread(profileTask).start();
    }
}
