package gal.usc.etse.sharecloud.client.gui_controller;

import gal.usc.etse.sharecloud.client.FachadaGUI;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;
import java.util.Set;
import gal.usc.etse.sharecloud.client.clientModel.User;

@Component
public class cLog {
    @FXML
    private TextField fieldEmail;
    @FXML
    private PasswordField fieldPassword;

    private String email;
    private FachadaGUI fgui;
    private HostServices hostServices;

    public void setFachadas(FachadaGUI fgui){this.fgui=fgui;}
    public void setHostServices(HostServices hostServices) {this.hostServices = hostServices;}
    public HostServices getHostServices(){return this.hostServices;}

    @FXML
    public void clickLogIn(){
        String email = fieldEmail.getText();
        String password = fieldPassword.getText();

        if (!email.isBlank() || !password.isBlank()) {
            try {
                User tempUser =
                        new User(email, password);

                User loggedUser = fgui.getAuthService().login(tempUser);


                fgui.entrarSesion(loggedUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void clickSignUp() {
        String email = fieldEmail.getText();
        String password = fieldPassword.getText();

        if (!email.isBlank() || !password.isBlank()) {
            try {
                gal.usc.etse.sharecloud.server.model.dto.User newUser =
                        new gal.usc.etse.sharecloud.server.model.dto.User(email, password, Set.of("USER"));

                fgui.getUserService().create(newUser);
                this.email = email;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
