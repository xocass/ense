package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.ShareCloudBoot;
import gal.usc.etse.sharecloud.http.AuthApi;
import gal.usc.etse.sharecloud.http.FriendApi;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;
import gal.usc.etse.sharecloud.http.SpotifyApi;

import javafx.concurrent.Task;
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

import java.awt.*;
import java.net.URI;


public class cProfile {
    private String userEmail;
    private SpotifyProfile loggedUser;

    private SpotifyApi spotifyApi;

    private String profileUserId;
    private Boolean isFriend;
    private Boolean isPending;
    @FXML private ImageView pfp;
    @FXML private Label username;
    @FXML private Label country;
    @FXML private Label followers;
    @FXML private VBox recentlyPlayedBox;
    @FXML private HBox topArtistBox;
    @FXML private HBox topTracksBox;
    @FXML private Button followSpoty;

    @FXML private Button btnFriendRequest;
    @FXML private ImageView imgFriendAction;
    @FXML private Button btnGoSpotify;

    private Boolean seguido;
    public String spotifyURL;

    public void setLoggedUser(SpotifyProfile loggedUser){this.loggedUser=loggedUser;}
    public void setUserEmail(String userEmail){this.userEmail=userEmail;}
    public void setProfileUserId(String profileUserId){this.profileUserId=profileUserId;}
    public void setIsFriend(Boolean isFriend){this.isFriend=isFriend;}
    public void setPfp(Image pfp){this.pfp.setImage(pfp);}
    public void setUsername(String username){this.username.setText(username);}
    public void setCountry(String country){this.country.setText(country);}
    public void setFollowers(Integer followers){this.followers.setText(followers.toString()+" followers");}
    public void setSeguido(Boolean seguido){
        this.seguido=seguido;
        if(this.seguido)
            followSpoty.setText("Dejar de seguir");
        else
            followSpoty.setText("Seguir en spotify");
    }
    public void setSpotifyURL(String spotifyURL){this.spotifyURL=spotifyURL;}
    public void setIsPending(Boolean isPending){this.isPending=isPending;}

    public Boolean getSeguido(){return seguido;}
    public Boolean getIsFriend(){return isFriend;}
    public Button getBtnGoSpotify(){return btnGoSpotify;}
    public Button getBtnFriendRequest(){return btnFriendRequest;}
    public ImageView getImgFriendAction(){return imgFriendAction;}

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
    public void clickOnFriendRequest(){
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (!isFriend) {
                    FriendApi.sendRequest(profileUserId);
                    isFriend = false;
                    isPending= true;
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            updateFriendButton();
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    @FXML
    public void clickOnGoSpotify(){
        if (spotifyURL == null || spotifyURL.isBlank()) {
            return;
        }

        try {
            Desktop.getDesktop().browse(new URI(spotifyURL));

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void updateFriendButton() {
        String iconPath =  "/gal/usc/etse/sharecloud/imgs/icon-friendAdd.png";

        imgFriendAction.setImage(new Image(ShareCloudBoot.class.getResourceAsStream(iconPath)));

        if(isPending || isFriend){
            btnFriendRequest.setDisable(true);
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
        cMenu.renderFriends(vboxFriends, userEmail);
        cMenu.configurarBusqueda(fieldSearch, vboxResults, userEmail);
        cMenu.configurarNotificaciones(btnNotification, userEmail);
    }
    @FXML
    private void clickOnNotification(){
        cMenu.abrirNotificaciones(userEmail);
    }
    @FXML
    private void clickViewProfile(){
        FachadaGUI.getInstance().mostrarPantallaCarga();
        cMenu.clickOnUserProfile(userEmail);
    }
    @FXML
    private void clickViewFeed() {
        cMenu.viewFeed(userEmail);
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