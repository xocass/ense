package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.ShareCloudBoot;
import gal.usc.etse.sharecloud.http.*;

import gal.usc.etse.sharecloud.model.dto.UserSearchResult;
import gal.usc.etse.sharecloud.model.entity.FeedItem;
import gal.usc.etse.sharecloud.model.entity.FeedState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class cFeed {
    private String userEmail;

    public void setEmail(String email) {this.userEmail = email;}

    //Feed
    private List<FeedItem> feedItems= new ArrayList<>();

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
    @FXML private HBox hboxFeedProfile;
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
        //cMenu.cargarAmigos(vboxFriends, userEmail);
        cMenu.configurarBusqueda(fieldSearch, vboxResults, userEmail);
        cMenu.configurarNotificaciones(btnNotification, userEmail);
    }

    public void initFeed(List<FeedItem> ignored, List<UserSearchResult> friends) {
        renderCurrentItem();
        cMenu.renderFriends(vboxFriends, userEmail);
    }
    private void renderCurrentItem() {
        FeedItem item = FeedState.getCurrent();

        if (item == null) {
            showEmptyFeed();
            return;
        }

        artCover.setImage(new Image(item.getTrack().getImageUrl()));
        titleLabel.setText(item.getTrack().getTrackName());
        artistLabel.setText(String.join(", ", item.getTrack().getArtists()));

        labelFriendName.setText(item.getSpotifyProfile().getDisplayName());
        labelDate.setText(cMenu.formatPlayedAt(item.getTrack().getPlayedAt()));

        if (item.getSpotifyProfile().getImage() != null) {
            friendProfilePic.setImage(new Image(item.getSpotifyProfile().getImage()));
        }

        btnBack.setDisable(!FeedState.hasPrev());
        btnNext.setDisable(!FeedState.hasNext());
    }

    private void showEmptyFeed() {
        friendProfilePic.setVisible(false);
        labelFriendName.setVisible(false);
        labelDate.setVisible(false);
        btnLike.setVisible(false);
        btnComment.setVisible(false);
        artCover.setImage(new Image((ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/imgs/boladesierto.gif").toExternalForm())));
        titleLabel.setText("Nada que ver por aquí....");
    }

    @FXML
    public void nextItem() {
        FeedState.next();
        renderCurrentItem();
    }

    @FXML
    public void previousItem() {
        FeedState.prev();
        renderCurrentItem();
    }

    @FXML
    public void like(){
        NotificationApi.likeTrack(FeedState.getCurrent().getId(),titleLabel.getText());
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
