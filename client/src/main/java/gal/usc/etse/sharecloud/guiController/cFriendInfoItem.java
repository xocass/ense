package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.FriendApi;
import gal.usc.etse.sharecloud.model.dto.FriendRequest;
import gal.usc.etse.sharecloud.model.entity.FriendRequestStatus;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class cFriendInfoItem {
    @FXML private ImageView imgAvatar;
    @FXML private Label labelMessage;

    private FriendRequest notification;
    private VBox parent;
    private Node root;
    private String userEmail;
    private String receiverId;

    public void setEmail(String userEmail) {this.userEmail = userEmail;}


    public void setNotification(FriendRequest notification, VBox parent, Node root) {
        this.notification = notification;
        this.parent = parent;
        this.root = root;
        this.receiverId= notification.receiverId();

        labelMessage.setText(buildMessage(notification));

        if (notification.receiverImage() != null) {
            imgAvatar.setImage(new Image(notification.receiverImage()));
        }
    }

    @FXML
    private void clickOnProfilePic(){
        FachadaGUI.getInstance().mostrarPantallaCarga();
        cMenu.clickOnOtherProfile(receiverId, userEmail);
    }
    @FXML
    private void clickOnUsername(){
        FachadaGUI.getInstance().mostrarPantallaCarga();
        cMenu.clickOnOtherProfile(receiverId, userEmail);
    }

    private String buildMessage(FriendRequest notif) {
        return switch (notif.status()) {
            case FriendRequestStatus.PENDING ->
                    "Solicitud de amistad enviada a " + notif.receiverName();
            case FriendRequestStatus.ACCEPTED ->
                    notif.receiverName() + " ha aceptado tu solicitud de amistad";
            case FriendRequestStatus.REJECTED ->
                    notif.receiverName() + " ha rechazado tu solicitud de amistad";
            default -> "";
        };
    }

    @FXML
    private void clickDismiss() {
        new Thread(() -> {
            try {
                FriendApi.sawFriendRequest(notification.id());
                Platform.runLater(() -> parent.getChildren().remove(root));

            } catch (Exception e) {e.printStackTrace();}
        }).start();
    }

}
