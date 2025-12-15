package gal.usc.etse.sharecloud.guiController;
import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;
import gal.usc.etse.sharecloud.http.SpotifyApi;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class cProfile {
    private FachadaGUI fgui;
    private SpotifyProfile loggedUser;

    private SpotifyApi spotifyApi;

    @FXML
    private ImageView pfp;
    @FXML
    private Label username;
    @FXML
    private Label country;
    @FXML
    private Label followers;
    @FXML
    private Label currUsername;
    @FXML
    private ImageView currPfp;
    @FXML
    private VBox recentlyPlayedBox;
    @FXML
    private HBox topArtistBox;
    @FXML
    private HBox topTracksBox;

    public String spotifyURL;

    public void setFachadas(FachadaGUI fgui, SpotifyProfile loggedUser){this.fgui=fgui; this.loggedUser=loggedUser;}
    public void setPfp(Image pfp){this.pfp.setImage(pfp);}
    public void setUsername(String username){this.username.setText(username);}
    public void setCountry(String country){this.country.setText(country);}
    public void setFollowers(Integer followers){this.followers.setText(followers.toString()+" followers");}
    public void setCurrUsername(String currUsername){this.currUsername.setText(currUsername);}
    public void setCurrPfp(Image currPfp){this.currPfp.setImage(currPfp);}

    //Vaciar boxes
    public void clearRecentlyPlayed(){
        recentlyPlayedBox.getChildren().clear();
    }
    public void clearTopArtist(){
        topArtistBox.getChildren().clear();
    }
    public void clearTopTracks(){
        topTracksBox.getChildren().clear();
    }

    //Insertar template en boxes
    public void addRecentlyPlayed(Parent template){
        recentlyPlayedBox.getChildren().add(template);
    }
    public void addTopArtist(Parent template){
        topArtistBox.getChildren().add(template);
    }
    public void addTopTrack(Parent template){
        topTracksBox.getChildren().add(template);
    }



}