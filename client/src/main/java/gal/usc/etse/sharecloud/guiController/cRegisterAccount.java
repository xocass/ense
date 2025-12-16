package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class cRegisterAccount {
    @FXML private TextField fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private PasswordField fieldRepeatPassword;
    @FXML private Label statusLabel;
    @FXML private Hyperlink linkLogin;


    @FXML
    public void initialize() {statusLabel.setText("");}

    @FXML
    private void clickOnGoBack() {
        FachadaGUI.getInstance().iniciarSesion(0);
    }

    @FXML
    private void clickOnContinue() {
        statusLabel.setText("");

        String email = fieldEmail.getText();
        String password = fieldPassword.getText();
        String repeatPassword = fieldRepeatPassword.getText();

        if (email.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
            statusLabel.setText("Email y contraseñas requeridas.");
            return;
        }
        if (!password.equals(repeatPassword)) {
            updateStatus("Las contraseñas no coinciden.");
            return;
        }

        try {
            int status = AuthApi.register(email, password);

            if (status == 201) {
                FachadaGUI.getInstance().registroVincularSpotify(email);

            } else if (status == 409) {
                updateStatus("El email ya está registrado.");
            } else {
                updateStatus("Error en el registro. Código: " + status);
            }

        } catch (Exception e) {
            updateStatus("Error de conexión: " + e.getMessage());
        }
    }

    private void updateStatus(String msg) {Platform.runLater(() -> statusLabel.setText(msg));}
}
