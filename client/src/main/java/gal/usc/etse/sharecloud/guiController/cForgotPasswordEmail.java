package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class cForgotPasswordEmail {
    private FachadaGUI fgui;

    @FXML private TextField fieldEmail;
    @FXML private Label statusLabel;


    public void setFachadas(FachadaGUI fgui) {this.fgui = fgui;}


    @FXML
    private void clickOnSendCode() {
        String email = fieldEmail.getText().trim();
        if (email.isBlank()) {
            updateStatus("Introduce un correo electrónico");
            return;
        }

        try {
            AuthApi.sendRecoveryCode(email);

            fgui.recuperarContrasenhaCodigo(email);
        } catch (Exception e) {
            updateStatus("Ha ocurrido un error al enviar el código. Inténtalo de nuevo");
        }

    }

    @FXML
    private void clickOnGoBack(){
            fgui.iniciarSesion();
    }

    private void updateStatus(String msg) {Platform.runLater(() -> statusLabel.setText(msg));}
}
