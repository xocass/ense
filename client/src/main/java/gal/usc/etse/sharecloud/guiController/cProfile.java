package gal.usc.etse.sharecloud.guiController;
import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;
import gal.usc.etse.sharecloud.http.SpotifyApi;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class cProfile {
    private FachadaGUI fgui;
    private SpotifyProfile loggedUser;

    private SpotifyApi spotifyApi;

    @FXML
    public ImageView pfpView;
    @FXML
    public Text usernameLabel;
    @FXML
    public Text countryLabel;
    @FXML
    public VBox recentlyPlayedBox;
    @FXML
    public HBox topArtistBox;
    @FXML
    public HBox topTrackBox;

    public void setFachadas(FachadaGUI fgui, SpotifyProfile loggedUser){this.fgui=fgui; this.loggedUser=loggedUser;}

}