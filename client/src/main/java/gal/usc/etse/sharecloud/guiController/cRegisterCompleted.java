package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class cRegisterCompleted {
    private FachadaGUI fgui;
    @FXML private Button btnGoBack;

    public void setFachadas(FachadaGUI fgui) {this.fgui=fgui;}

    @FXML
    public void clickOnGoBack(){
        fgui.iniciarSesion();
    }
}
