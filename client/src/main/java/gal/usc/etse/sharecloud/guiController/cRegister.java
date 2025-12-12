package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class cRegister {
    @FXML private TextField fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private Label statusLabel;

    private FachadaGUI fgui;
    private cLog cLog;

    public void setFachadas(FachadaGUI fgui, cLog cLog) {this.fgui = fgui; this.cLog = cLog;}

    @FXML
    private void clickOnCancel() {
        returnToLogin();
    }

    @FXML
    private void clickOnRegister() {
        statusLabel.setText("");

        String email = fieldEmail.getText();
        String password = fieldPassword.getText();

        if (email.isBlank() || password.isBlank()) {
            statusLabel.setText("Email y contraseña requeridos.");
            return;
        }

        new Thread(() -> {
            try {
                int status = AuthApi.register(email, password);

                if (status == 201) {
                    Platform.runLater(() -> {
                        statusLabel.setText("Cuenta creada correctamente.");
                        showSpotifyLinkPopup(email);
                    });

                } else if (status == 409) {
                    updateStatus("El email ya está registrado.");
                } else {
                    updateStatus("Error en el registro. Código: " + status);
                }

            } catch (Exception e) {
                updateStatus("Error de conexión: " + e.getMessage());
            }
        }).start();
    }

    private void showSpotifyLinkPopup(String email) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/vLinkSpotify.fxml")
            );
            Parent root = fxmlLoader.load();

            Stage popup = new Stage();
            popup.setScene(new Scene(root));
            popup.setTitle("Vincular cuenta de Spotify");
            popup.setResizable(false);

            cLinkSpotify controller = fxmlLoader.getController();
            controller.setEmail(email);
            controller.setFachadas(this);

            // Hacerla modal
            popup.initOwner(fgui.getEntrarStage());
            popup.initModality(Modality.WINDOW_MODAL);

            popup.show();
        } catch (Exception e) {e.printStackTrace();}
    }

    private void updateStatus(String msg) {
        javafx.application.Platform.runLater(() -> statusLabel.setText(msg));
    }

    public void closeWindow() {
        ((Stage) fieldEmail.getScene().getWindow()).close();
    }

    public void returnToLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/vLog.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 878, 422);
            cLog controller = fxmlLoader.getController();
            controller.setFachadas(this.fgui);

            fgui.getEntrarStage().setTitle("Iniciar sesión");
            fgui.getEntrarStage().setScene(scene);
            fgui.getEntrarStage().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
