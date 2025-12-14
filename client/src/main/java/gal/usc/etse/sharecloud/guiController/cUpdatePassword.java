package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class cUpdatePassword {
    @FXML
    private Label statusLabel;
    @FXML private PasswordField fieldNewPassword;
    @FXML private PasswordField fieldPassword2;

    private FachadaGUI fgui;
    private String email;

    public void setFachadas(FachadaGUI fgui) {this.fgui = fgui;};
    public void setEmail(String email) {this.email = email;}


    @FXML
    private void initialize() {statusLabel.setText("");}


    @FXML
    private void clickOnUpdatePassword() {
        String newPassword = fieldNewPassword.getText();
        String password2 = fieldPassword2.getText();

        if (newPassword.isEmpty() || password2.isEmpty()) {
            updateStatus("Escriba en los campos.");
            return;
        }else if(!newPassword.equals(password2)) {
            updateStatus("Las contraseñas no coinciden.");
            return;
        }

        try {
            if(AuthApi.updatePassword(email, newPassword)){
                returnToLogin();
            }else{
                updateStatus("No se ha podido actualizar la contraseña.");
            }
        } catch (Exception e) {
            updateStatus("ERROR: " + e.getMessage());
        }

    }

    @FXML
    private void clickOnGoBack(){returnToLogin();}

    private void returnToLogin() {
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
