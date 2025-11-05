package gal.usc.etse.sharecloud.gui_controller;

import gal.usc.etse.sharecloud.Boot;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class cLog {
    @FXML
    private TextField fieldEmail;
    @FXML
    private PasswordField fieldPassword;
    @FXML
    private Button btnIniciarSesion;
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
    private Button btnRegistrarse;
    @FXML
    private Label labelOkReg;
    @FXML
    private Label labelNopeReg;

    private Boot fgui;
    private HostServices hostServices;

    public void setFachadas(Boot fgui){this.fgui=fgui;}
    public void setHostServices(HostServices hostServices) {this.hostServices = hostServices;}
    public HostServices getHostServices(){return this.hostServices;}

    @FXML
    public void clickLogIn(){
        String email= fieldEmail.getText();
        String password= fieldPassword.getText();

        labelOk.setVisible(true);
    }

    @FXML
    public void clickSignUp(){
        String email= fieldEmail.getText();
        String password= fieldPassword.getText();
        String username= fieldNombre.getText();
        String edad= fieldEdad.getText();
        String ciudad= fieldCiudad.getText();
        String pais= fieldPais.getText();

        labelOkReg.setVisible(true);
    }
}
