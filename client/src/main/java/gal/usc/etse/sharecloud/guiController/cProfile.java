package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;
import gal.usc.etse.sharecloud.http.SpotifyApi;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;



public class cProfile {
    private String userEmail;
    private SpotifyProfile loggedUser;

    private SpotifyApi spotifyApi;

    @FXML private ImageView pfp;
    @FXML private Label username;
    @FXML private Label country;
    @FXML private Label followers;
    //@FXML private Label currUsername;
    //@FXML private ImageView currPfp;
    @FXML private VBox recentlyPlayedBox;
    @FXML private HBox topArtistBox;
    @FXML private HBox topTracksBox;
    @FXML private Button followSpoty;

    private Boolean seguido;
    public String spotifyURL;

    public void setLoggedUser(SpotifyProfile loggedUser){this.loggedUser=loggedUser;}
    public void setUserEmail(String userEmail){this.userEmail=userEmail;}
    public void setPfp(Image pfp){this.pfp.setImage(pfp);}
    public void setUsername(String username){this.username.setText(username);}
    public void setCountry(String country){this.country.setText(country);}
    public void setFollowers(Integer followers){this.followers.setText(followers.toString()+" followers");}
    //public void setCurrUsername(String currUsername){this.currUsername.setText(currUsername);}
    //public void setCurrPfp(Image currPfp){this.currPfp.setImage(currPfp);}
    public void setSeguido(Boolean seguido){
        this.seguido=seguido;
        if(this.seguido)
            followSpoty.setText("Dejar de seguir");
        else
            followSpoty.setText("Seguir en spotify");
    }

    public Boolean getSeguido(){return seguido;}

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

    @FXML
    public void clickFollowUnfollow(){
        if(seguido){
            FachadaGUI.getInstance().doUnfollow(loggedUser.getSpotifyId());
            setSeguido(false);
        }else {
            FachadaGUI.getInstance().doFollow(loggedUser.getSpotifyId());
            setSeguido(true);
        }
    }







    /*  ###################################################################  */
    /*  ########  FUNCIONALIDADES E INTERACIONES CON MENU LATERAL  ########  */
    /*  ###################################################################  */
    @FXML private Label menuUsername;
    @FXML private ImageView menuUserPicture;

    public void setMenuUsername(String username) {this.menuUsername.setText(username);}
    public ImageView getMenuUserPicture() {return menuUserPicture;}

    @FXML private ImageView btnNotification;
    @FXML private Button btnFriends;
    @FXML private Button btnSearch;
    @FXML private ScrollPane friendsPane;
    @FXML private VBox vboxFriends;
    @FXML private VBox searchPane;
    @FXML private TextField fieldSearch;
    @FXML private VBox vboxResults;

    @FXML
    public void initialize() {
        cMenu.activarAmigos(btnFriends, btnSearch, friendsPane, searchPane);
        cMenu.cargarAmigos(vboxFriends, userEmail);
        cMenu.configurarBusqueda(fieldSearch, vboxResults, userEmail);
        cMenu.configurarNotificaciones(btnNotification);
    }
    @FXML
    private void clickOnNotification(){
        cMenu.abrirNotificaciones();
    }
    @FXML
    private void clickViewProfile(){
        FachadaGUI.getInstance().mostrarPantallaCarga();
        cMenu.clickOnUserProfile(userEmail);
    }
    @FXML
    private void clickViewFeed(){
        FachadaGUI.getInstance().mostrarPantallaCarga();
        cMenu.clickViewFeed(userEmail);
    }
    @FXML
    private void clickOnLogout(){
        try {
            AuthApi.logout(userEmail);
            FachadaGUI.getInstance().iniciarSesion(0);
        }catch(Exception e){e.printStackTrace();}
    }
    @FXML
    private void clickOnFriends(){
        cMenu.activarAmigos(btnFriends, btnSearch, friendsPane, searchPane);
    }
    @FXML
    private void clickOnSearch(){
        cMenu.activarBuscar(btnFriends, btnSearch, friendsPane, searchPane);
    }
    @FXML
    private void clickOnSearchIcon(){
        cMenu.buscarUsuarios(vboxResults, fieldSearch, userEmail);
    }

}