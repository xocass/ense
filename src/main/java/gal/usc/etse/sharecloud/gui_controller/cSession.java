package gal.usc.etse.sharecloud.gui_controller;

import gal.usc.etse.sharecloud.FachadaGUI;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URI;

@Component
public class cSession {
    private FachadaGUI fgui;
    private HostServices hostServices;
    private gal.usc.etse.sharecloud.model.dto.User loggedUser;

    public void setFachadas(FachadaGUI fgui, gal.usc.etse.sharecloud.model.dto.User loggedUser){this.fgui=fgui; this.loggedUser=loggedUser;}
    public void setHostServices(HostServices hostServices) {this.hostServices = hostServices;}
    public HostServices getHostServices(){return this.hostServices;}

    @FXML
    public void clickLinkSpotify(){
        try {
            String url = fgui.getUserService().startSpotifyLink(loggedUser.email());

            fgui.getHostServices().showDocument(url);
        } catch (Exception e) {e.printStackTrace();}
    }

    @FXML
    public void clickSearchFriends(){

    }
}
