package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class cRegisterCompleted {
    @FXML private Button btnGoBack;


    @FXML
    public void clickOnGoBack(){
        FachadaGUI.getInstance().iniciarSesion(0);
    }
}
