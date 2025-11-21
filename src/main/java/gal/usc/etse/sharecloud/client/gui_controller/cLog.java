package gal.usc.etse.sharecloud.client.gui_controller;

import gal.usc.etse.sharecloud.client.FachadaGUI;
import gal.usc.etse.sharecloud.client.clientModel.dto.AuthResponse;
import gal.usc.etse.sharecloud.client.http.AuthApi;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;
import gal.usc.etse.sharecloud.client.clientModel.User;

@Component
public class cLog {
    @FXML
    private TextField fieldEmail;
    @FXML
    private PasswordField fieldPassword;

    private final AuthApi authApi = new AuthApi();
    private FachadaGUI fgui;

    public void setFachadas(FachadaGUI fgui){this.fgui=fgui;}

    @FXML
    public void clickLogIn(){
        String email = fieldEmail.getText();
        String password = fieldPassword.getText();

        if (!email.isBlank() || !password.isBlank()) {
            try {
                AuthResponse user = authApi.login(email, password);
                User loggedUser= User.from(user);
                fgui.entrarSesion(loggedUser);

            } catch (Exception e) {e.printStackTrace();}
        }
    }

    @FXML
    public void clickSignUp() {
        String email = fieldEmail.getText();
        String password = fieldPassword.getText();

        if (!email.isBlank() || !password.isBlank()) {
            try {
                Integer code= authApi.register(email, password);
                if(code != 201){
                    System.out.println("Register failed");
                }
            } catch (Exception e) {e.printStackTrace();}
        }
    }
}
