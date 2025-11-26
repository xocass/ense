package gal.usc.etse.sharecloud.gui_controller;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.clientModel.User;
import gal.usc.etse.sharecloud.http.SpotifyApi;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class cProfile {
    private FachadaGUI fgui;
    private User loggedUser;

    private SpotifyApi spotifyApi;

    @FXML
    public ImageView pfpView;
    @FXML
    public Text usernameLabel;
    @FXML
    public Text countryLabel;

    public void setFachadas(FachadaGUI fgui, User loggedUser){this.fgui=fgui; this.loggedUser=loggedUser;}
    public void initSpotifyApi(String email, String jwtToken) {spotifyApi = new SpotifyApi(email, jwtToken);}

}
