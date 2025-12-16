package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class cPopUpWarning {
    @FXML private Label labelWarning;
    @FXML private Label messageWarming;
    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    public Label getLabelWarning(){return labelWarning;}
    public Label getMessageWarming(){return messageWarming;}

    @FXML
    public void initialize(){

    }

    @FXML
    public void clickOnCancel(){
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
    @FXML
    public void clickOnConfirm(){
        FachadaGUI.getInstance().iniciarSesion(0);
    }


}
