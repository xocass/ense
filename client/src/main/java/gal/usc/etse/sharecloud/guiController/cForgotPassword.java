package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class cForgotPassword {
    private FachadaGUI fgui;
    private cLog cLog;

    @FXML private TextField fieldEmail;
    @FXML private Label statusLabel;


    public void setFachadas(FachadaGUI fgui, cLog cLog) {this.fgui = fgui; this.cLog = cLog;}

    @FXML
    private void initialize() {
        statusLabel.setText("");
    }

    @FXML
    private void clickOnSendCode() {
        String email = fieldEmail.getText().trim();
        if (email.isEmpty()) {
            statusLabel.setText("Introduce un correo electrónico");
            return;
        }

        try {
            AuthApi.sendRecoveryCode(email);

            showInsertRecoveryCode(email);
        } catch (Exception e) {
            updateStatus("Ha ocurrido un error al enviar el código. Inténtalo de nuevo");
        }

    }

    private void showInsertRecoveryCode(String email) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/vInsertCode.fxml")
            );

            Scene scene = new Scene(fxmlLoader.load(), 467, 422);
            cInsertRecoveryCode controller = fxmlLoader.getController();
            controller.setEmail(email);
            controller.setFachadas(this.fgui);

            fgui.getEntrarStage().setTitle("Recuperar contraseña");
            fgui.getEntrarStage().setScene(scene);
            fgui.getEntrarStage().show();
        } catch (Exception e) {e.printStackTrace();}
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
    private void updateStatus(String msg) {
        Platform.runLater(() -> statusLabel.setText(msg));
    }
}
