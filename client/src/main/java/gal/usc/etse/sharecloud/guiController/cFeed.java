package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;
import gal.usc.etse.sharecloud.http.UserApi;
import gal.usc.etse.sharecloud.model.dto.UserSearchResult;

import javafx.application.Platform;
import javafx.fxml.FXML;
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

import javax.swing.text.PlainDocument;
import java.io.IOException;
import java.util.List;

public class cFeed {
    private FachadaGUI fgui;
    private String email;

    public void setEmail(String email) {this.email = email;}
    public void setFachadas(FachadaGUI fgui) {this.fgui = fgui;}

    @FXML private ImageView btnNotification;
    @FXML private Button btnFriends;
    @FXML private Button btnSearch;
    @FXML private ScrollPane friendsPane;
    @FXML private VBox vboxFriends;
    @FXML private VBox searchPane;
    @FXML private TextField fieldSearch;
    @FXML private VBox vboxResults;



    @FXML
    public void initialize() {
        activarAmigos();
        cargarAmigos();
        configurarBusqueda();
        configurarNotificaciones();
    }

    private void activarAmigos() {
        btnFriends.getStyleClass().setAll("pill-button-active");
        btnSearch.getStyleClass().setAll("pill-button");

        friendsPane.setVisible(true);
        friendsPane.setManaged(true);

        searchPane.setVisible(false);
        searchPane.setManaged(false);
    }
    private void activarBuscar() {
        btnFriends.getStyleClass().setAll("pill-button");
        btnSearch.getStyleClass().setAll("pill-button-active");

        friendsPane.setVisible(false);
        friendsPane.setManaged(false);

        searchPane.setVisible(true);
        searchPane.setManaged(true);
    }
    private void configurarBusqueda(){
        fieldSearch.setOnAction(e -> buscarUsuarios());
    }

    @FXML
    private void clickViewProfile(){
        fgui.verCurrPerfil();
    }



    private void cargarAmigos() {
        try {
            vboxFriends.getChildren().clear();

            List<UserSearchResult> amigos = UserApi.getFriends();

            if (amigos.isEmpty()) {
                Label empty = new Label("Aún no tienes amigos añadidos");
                empty.getStyleClass().add("side-empty-text");
                vboxFriends.getChildren().add(empty);
                return;
            }

            for (UserSearchResult amigo : amigos) {
                Platform.runLater(()->{
                    vboxFriends.setFillWidth(true);
                    showResults(vboxResults, amigo, true);});
            }
        }catch (Exception e){e.printStackTrace();}

    }

    private void buscarUsuarios() {
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

            for (UserSearchResult user : resultados) {
                Platform.runLater(()->{
                    vboxResults.setFillWidth(true);
                    showResults(vboxResults, user, user.isFriend());});
            }
        } catch (Exception e) {e.printStackTrace();}

    }
    private void showResults(VBox container, UserSearchResult user, boolean isFriend) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/templateUserItem.fxml")
            );
            HBox item = loader.load();

            cUserItem controller = loader.getController();
            controller.setLabelName(user.username());
            controller.setLabelCountry(user.country());
            controller.setIsFriend(isFriend);
            controller.setUserId(user.id());
            if(user.image() != null){
                Image pic = new Image(user.image());
                controller.getProfilePic().setImage(pic);
            }
            item.setOnMouseClicked((event) -> {
                fgui.verOtroPerfil(user.id());
            });
            container.getChildren().add(item);
        } catch (IOException e) {e.printStackTrace();}
    }


    @FXML
    private void clickOnNotification(){
        abrirNotificaciones();
    }
    private void configurarNotificaciones() {
        btnNotification.setOnMouseClicked(e -> abrirNotificaciones());
    }

    private void abrirNotificaciones() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/popUpNotification.fxml")
            );
            Parent root = fxmlLoader.load();

            Stage popup = new Stage();
            popup.setScene(new Scene(root));
            popup.setTitle("Notificaciones");
            popup.setResizable(false);

            cPopUpNotification controller = fxmlLoader.getController();
            controller.setFachadas(this.fgui);

            // Hacerla modal
            popup.initOwner(fgui.getEntrarStage());
            popup.initModality(Modality.WINDOW_MODAL);
            popup.initStyle(StageStyle.TRANSPARENT);

            popup.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clickOnLogout(){
        try {
            AuthApi.logout(email);
            fgui.iniciarSesion();
        }catch(Exception e){System.err.println("Error cierre sesión: "+e.getMessage());}
    }

    @FXML
    private void clickOnFriends(){
        activarAmigos();
    }
    @FXML
    private void clickOnSearch(){
        activarBuscar();
    }

    @FXML
    private void clickOnSearchIcon(){
        buscarUsuarios();
    }

}
