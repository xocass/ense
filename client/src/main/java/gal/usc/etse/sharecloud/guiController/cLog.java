package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;
import gal.usc.etse.sharecloud.model.dto.AuthRequest;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class cLog {
    @FXML private TextField fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private Label statusLabel;

    private FachadaGUI fgui;


    public void setFachadas(FachadaGUI fgui){this.fgui=fgui;}


    @FXML
    private void clickOnLogin() {
        String email = fieldEmail.getText();
        String password = fieldPassword.getText();

        if (email.isBlank() || password.isBlank()) {
            statusLabel.setText("Email y contraseña requeridos.");
            return;
        }


            try {
                int status = AuthApi.login(email, password);

                if (status == 200) {
                    updateStatus("Login correcto.");
                    fgui.entrarSesion(email);

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
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/vRegister.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 878, 422);
            cRegister controller = fxmlLoader.getController();
            controller.setFachadas(this.fgui, this);

            fgui.getEntrarStage().setTitle("Registro");
            fgui.getEntrarStage().setScene(scene);
            fgui.getEntrarStage().show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());}
    }

    private void updateStatus(String msg) {
        javafx.application.Platform.runLater(() -> statusLabel.setText(msg));
    }
}
