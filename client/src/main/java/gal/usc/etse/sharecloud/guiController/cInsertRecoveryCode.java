package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class cInsertRecoveryCode {
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
    private void clickOnCheckCode() {
        String code = fieldCode.getText().trim();
        if (code.isEmpty()) {
            updateStatus("Introduce el código de verificación recibido.");
            return;
        }

        try {
            if(AuthApi.checkRecoveryCode(email, code)){
                showUpdatePassword(email);

            }else{
                updateStatus("Código incorrecto.");
            }
        } catch (Exception e) {
            updateStatus("Ha ocurrido un error al enviar el código. Inténtalo de nuevo");
        }
    }

    private void showUpdatePassword(String email) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/vUpdatePassword.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 467, 422);
            cUpdatePassword controller = fxmlLoader.getController();
            controller.setFachadas(this.fgui);
            controller.setEmail(email);

            fgui.getEntrarStage().setTitle("Iniciar sesión");
            fgui.getEntrarStage().setScene(scene);
            fgui.getEntrarStage().show();
        } catch (Exception e) {
            e.printStackTrace();
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

    private void updateStatus(String msg) {Platform.runLater(() -> statusLabel.setText(msg));}
}
