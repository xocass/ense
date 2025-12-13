package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;
import javafx.fxml.FXML;

public class cSession {
    private String email;
    private FachadaGUI fgui;

    public void setEmail(String email) {this.email = email;}
    public void setFachadas(FachadaGUI fgui) {this.fgui = fgui;}


    @FXML
    private void clickSearchFriends(){

    }

    @FXML
    private void clickViewProfile(){
        fgui.verCurrPerfil();
    }

    @FXML
    private void clickOnLogout(){
        try {
            AuthApi.logout(email);
            fgui.iniciarSesion();
        }catch(Exception e){System.err.println("Error cierre sesión: "+e.getMessage());}
    }
}
