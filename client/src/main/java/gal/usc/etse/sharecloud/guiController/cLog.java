package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;

import gal.usc.etse.sharecloud.http.AuthApi;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class cLog {
    @FXML private TextField fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private Label statusLabel;
    @FXML private Hyperlink forgotPasswordLink;

    private FachadaGUI fgui;


    public void setFachadas(FachadaGUI fgui){this.fgui=fgui;}


    @FXML
    private void clickOnLogin() {
        String email = fieldEmail.getText();
        String password = fieldPassword.getText();

        if (email.isBlank() || password.isBlank()) {
            updateStatus("Email y contraseña requeridos.");
            return;
        }
        System.out.println("Email: " + email+ ". Password: " + password);

            try {
                int status = AuthApi.login(email, password);

                if (status == 200) {
                    updateStatus("Login correcto.");
                    fgui.irFeed(email);

                } else if (status == 401) {
                    updateStatus("Credenciales incorrectas.");
                } else {
                    updateStatus("Error en login. Código: " + status);
                }

            } catch (Exception e) {
                updateStatus("Error de conexión: " + e.getMessage());
            }

    }

    @FXML
    private void clickOnRegister() {
        fgui.registroCrearCuenta();
    }

    @FXML
    private void clickOnForgotPassword() {
        fgui.recuperarContrasenhaEmail();
    }

    private void updateStatus(String msg) {
        javafx.application.Platform.runLater(() -> statusLabel.setText(msg));
    }
}
