package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;
import gal.usc.etse.sharecloud.http.UserApi;
import gal.usc.etse.sharecloud.model.dto.UserSearchResult;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class cSession {
    private String email;
    private FachadaGUI fgui;

    @FXML private VBox resultsBox;
    @FXML private TextField fieldSearch;
    @FXML private Label statusLabel;

    public void setEmail(String email) {this.email = email;}
    public void setFachadas(FachadaGUI fgui) {this.fgui = fgui;}


    @FXML
    private void clickOnSearch(){
        String query = fieldSearch.getText().trim();
        if (query.isEmpty()) {
            resultsBox.getChildren().clear();
            return;
        }
        statusLabel.setText("Buscando...");

        try {
            List<UserSearchResult> results = UserApi.searchUsers(query);

            Platform.runLater(() -> {
                showResults(results);
            });
            updateStatus(results.isEmpty() ? "No se encontraron usuarios" : "");

        } catch (Exception e){
            System.out.println(e.getMessage());
            updateStatus("Error en la búsqueda");
        }
    }

    @FXML
    private void clickViewProfile(){
        fgui.verCurrPerfil();
    }

    @FXML
    private void clickOnLogout(){
        try {
            AuthApi.logout(email);
            fgui.iniciarSesion();
        }catch(Exception e){System.err.println("Error cierre sesión: "+e.getMessage());}
    }

    private void showResults(List<UserSearchResult> users) {
        resultsBox.getChildren().clear();

        for (UserSearchResult user : users) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/templateUserSearchResult.fxml")
                );
                HBox item = loader.load();

                cUserSearchResult controller = loader.getController();
                controller.setUserSearchResult(user);
                controller.setLabelUsername(user.username());
                Image pic = new Image(user.image());
                controller.getProfilePic().setImage(pic);

                resultsBox.getChildren().add(item);

            } catch (IOException e) {e.printStackTrace();}
        }
    }

    private void updateStatus(String msg) {
        javafx.application.Platform.runLater(() -> statusLabel.setText(msg));
    }
}
