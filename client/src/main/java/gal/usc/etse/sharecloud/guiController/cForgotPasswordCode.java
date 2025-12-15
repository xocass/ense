package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class cForgotPasswordCode {
    @FXML private TextField fieldCode;
    @FXML private Label statusLabel;

    private FachadaGUI fgui;
    private String email;

    public void setFachadas(FachadaGUI fgui) {this.fgui = fgui;}
    public void setEmail(String email) {this.email = email;}


    @FXML
    private void initialize() {
        statusLabel.setText("");
    }

    @FXML
    private void clickOnVerifyCode() {
        String code = fieldCode.getText().trim();
        if (code.isEmpty()) {
            updateStatus("Introduce el código de verificación recibido.");
            return;
        }

        try {
            if(AuthApi.checkRecoveryCode(email, code)){
                fgui.recuperarContrasenhaActualizar(email);

            }else{
                updateStatus("Código incorrecto.");
            }
        } catch (Exception e) {
            updateStatus("Ha ocurrido un error al enviar el código. Inténtalo de nuevo");
        }
    }

    @FXML
    private void clickOnResendCode() {
        if (!fieldCode.getText().isEmpty()) {
            fieldCode.setText("");
        }

        try {
            AuthApi.sendRecoveryCode(email);

        } catch (Exception e) {
            updateStatus("Ha ocurrido un error al reenviar el código. Inténtalo de nuevo");
        }
    }

    @FXML
    private void clickOnGoBack(){
        fgui.iniciarSesion();
    }

    private void updateStatus(String msg) {Platform.runLater(() -> statusLabel.setText(msg));}
}
