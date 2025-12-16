package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class cForgotPasswordUpdate {
    @FXML private Label statusLabel;
    @FXML private PasswordField fieldNewPassword;
    @FXML private PasswordField fieldRepeatPassword;

    private String email;

    public void setEmail(String email) {this.email = email;}


    @FXML
    private void initialize() {statusLabel.setText("");}


    @FXML
    private void clickOnSavePassword() {
        String newPassword = fieldNewPassword.getText();
        String password2 = fieldRepeatPassword.getText();

        if (newPassword.isEmpty() || password2.isEmpty()) {
            updateStatus("Escriba en los campos.");
            return;
        }else if(!newPassword.equals(password2)) {
            updateStatus("Las contraseñas no coinciden.");
            return;
        }

        try {
            if(AuthApi.updatePassword(email, newPassword)){
                FachadaGUI.getInstance().recuperarContrasenhaCompletado();
            }else{
                updateStatus("No se ha podido actualizar la contraseña.");
            }
        } catch (Exception e) {
            updateStatus("ERROR: " + e.getMessage());
        }

    }

    private void updateStatus(String msg) {Platform.runLater(() -> statusLabel.setText(msg));}

}
