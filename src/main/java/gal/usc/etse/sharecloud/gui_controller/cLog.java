package gal.usc.etse.sharecloud.gui_controller;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.exception.DuplicateUserException;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class cLog {
    @FXML
    private TextField fieldEmail;
    @FXML
    private PasswordField fieldPassword;
    @FXML
    private Label labelOk;
    @FXML
    private Label labelNope;
    @FXML
    private TextField fieldNombre;
    @FXML
    private TextField fieldEdad;
    @FXML
    private TextField fieldCiudad;
    @FXML
    private TextField fieldPais;
    @FXML
    private Label labelOkReg;
    @FXML
    private Label labelNopeReg;

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
                // Intentar login
                gal.usc.etse.sharecloud.model.dto.User tempUser =
                        new gal.usc.etse.sharecloud.model.dto.User(email, password, Set.of());

                gal.usc.etse.sharecloud.model.dto.User loggedUser = fgui.getAuthService().login(tempUser);

                // Si llega aquí, login correcto
                labelOk.setVisible(true);
                labelNope.setVisible(false);

                fgui.entrarSesion(loggedUser);
            } catch (Exception e) {
                e.printStackTrace();
                labelOk.setVisible(false);
                labelNope.setVisible(true);
            }
        }
    }

    @FXML
    public void clickSignUp(){
        String email = fieldEmail.getText();
        String password = fieldPassword.getText();

        if (!email.isBlank() || !password.isBlank()) {
            try {
                gal.usc.etse.sharecloud.model.dto.User newUser =
                        new gal.usc.etse.sharecloud.model.dto.User(email, password, Set.of("USER"));

                fgui.getUserService().create(newUser);  // Llama a UserService

                // Si llega aquí, usuario registrado correctamente (201)
                this.email = email;
                labelOkReg.setVisible(true);
                labelNopeReg.setVisible(false);

            } catch (DuplicateUserException e) {
                // Usuario ya existe (409)
                labelOkReg.setVisible(false);
                labelNopeReg.setVisible(true);

            } catch (IllegalArgumentException e) {
                // Datos inválidos (400)
                labelOkReg.setVisible(false);
                labelNopeReg.setVisible(true);

            } catch (Exception e) {
                // Otros errores generales (500)
                labelOkReg.setVisible(false);
                labelNopeReg.setVisible(true);
                e.printStackTrace();
            }
        }
    }
}
