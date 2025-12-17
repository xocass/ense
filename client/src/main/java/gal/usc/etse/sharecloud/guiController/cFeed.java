package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.ShareCloudBoot;
import gal.usc.etse.sharecloud.http.*;

import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyArtist;
import gal.usc.etse.sharecloud.model.entity.FeedItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class cFeed {
    private String userEmail;

    public void setEmail(String email) {this.userEmail = email;}

    //Feed
    private List<FeedItem> feedItems;


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


    //FEED
    @FXML private ImageView artCover;
    @FXML private Label titleLabel;
    @FXML private Label artistLabel;
    @FXML private Label labelFriendName;
    @FXML private Label labelDate;
    @FXML private ImageView friendProfilePic;
    @FXML private ImageView btnBack;
    @FXML private ImageView btnNext;
    @FXML private ImageView btnLike;
    @FXML private ImageView btnComment;

    public void setArtCover(Image artCover) {this.artCover.setImage(artCover);}
    public void setTitleLabel(String title) {this.titleLabel.setText(title);}
    public void setArtistLabel(String artists){this.artistLabel.setText(artists);}





    @FXML
    public void initialize() {
        cMenu.activarAmigos(btnFriends, btnSearch, friendsPane, searchPane);
        cMenu.cargarAmigos(vboxFriends, userEmail);
        cMenu.configurarBusqueda(fieldSearch, vboxResults, userEmail);
        cMenu.configurarNotificaciones(btnNotification, userEmail);
        cargarFeed(true);
    }


    //FEED
    public void cargarFeed(boolean actualizar){
        int feedCounter = cMenu.getFeedCounter();

        //Visibilizar los items
        friendProfilePic.setVisible(true);
        labelFriendName.setVisible(true);
        labelDate.setVisible(true);
        btnBack.setVisible(true);
        btnNext.setVisible(true);
        btnLike.setVisible(true);
        btnComment.setVisible(true);

        //Si estamos en el primer item
        if(feedCounter==0){
            btnBack.setVisible(false);
            if(actualizar)feedItems=FeedApi.loadFeed();
        }
        if(!feedItems.isEmpty()) {
            artCover.setImage(new Image(feedItems.get(feedCounter).getTrack().getImageUrl()));
            titleLabel.setText(feedItems.get(feedCounter).getTrack().getTrackName());
            String artistNames = String.join(
                    ", ",
                    feedItems.get(feedCounter)
                            .getTrack()
                            .getArtists()
            );
            artistLabel.setText(artistNames);

            labelFriendName.setText(feedItems.get(feedCounter).getSpotifyProfile().getDisplayName());
            labelDate.setText(formatPlayedAt(feedItems.get(feedCounter).getTrack().getPlayedAt()));
            if (feedItems.get(feedCounter).getSpotifyProfile().getImage() != null)
                friendProfilePic.setImage(new Image(feedItems.get(feedCounter).getSpotifyProfile().getImage()));
        }
        else{
            friendProfilePic.setVisible(false);
            labelFriendName.setVisible(false);
            labelDate.setVisible(false);
            btnBack.setVisible(false);
            btnNext.setVisible(false);
            btnLike.setVisible(false);
            btnComment.setVisible(false);
            artCover.setImage(new Image((getClass().getResource("/gal/usc/etse/sharecloud/imgs/nothing2see.png").toExternalForm())));
            titleLabel.setText("Parece que no hay nada que ver...");
        }
    }

    public static String formatPlayedAt(Instant playedAt) {
        if (playedAt == null) return "";

        return DateTimeFormatter.ofPattern("HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(playedAt);
    }

    @FXML
    public void nextItem(){
        cMenu.setFeedCounter(cMenu.getFeedCounter()+1);
        if(cMenu.getFeedCounter()==feedItems.size()){
            friendProfilePic.setVisible(false);
            labelFriendName.setVisible(false);
            labelDate.setVisible(false);
            btnLike.setVisible(false);
            btnComment.setVisible(false);
            artCover.setImage(new Image((getClass().getResource("/gal/usc/etse/sharecloud/imgs/boladesierto.gif").toExternalForm())));
            titleLabel.setText("Has llegado al final");
            artistLabel.setText("Puede que tengas algo nuevo para ver --->");
        }else if(cMenu.getFeedCounter()==feedItems.size()+1){
            cMenu.setFeedCounter(0);
            cargarFeed(true);
        }else{
            cargarFeed(true);
        }

    }

    @FXML
    public void previousItem(){
        if(cMenu.getFeedCounter()>0){
            cMenu.setFeedCounter(cMenu.getFeedCounter()-1);
            cargarFeed(false);
        }else {
            cMenu.setFeedCounter(cMenu.getFeedCounter()-1);
        }
    }


    /*  ###################################################################  */
    /*  ########  FUNCIONALIDADES E INTERACIONES CON MENU LATERAL  ########  */
    /*  ###################################################################  */
    @FXML
    private void clickViewProfile(){
        FachadaGUI.getInstance().mostrarPantallaCarga();
        cMenu.clickOnUserProfile(userEmail);
    }
    @FXML
    private void clickOnNotification(){
        cMenu.abrirNotificaciones(userEmail);
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
