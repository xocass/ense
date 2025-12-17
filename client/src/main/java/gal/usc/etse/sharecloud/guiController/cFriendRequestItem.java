package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.FriendApi;
import gal.usc.etse.sharecloud.model.dto.FriendRequest;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class cFriendRequestItem {
    @FXML private HBox root;
    @FXML private ImageView imgAvatar;
    @FXML private Label labelName;
    @FXML private Button btnAccept;
    @FXML private Button btnReject;

    private FriendRequest request;
    private String senderId;
    private String userEmail;

    public void setEmail(String userEmail) {this.userEmail = userEmail;}

    public void setRequest(FriendRequest request) {
        this.request = request;

        // Aqui los datos que queremos mostrar es de los senders de solicitudes.
        // en FriendRequest no hay campos senderName y senderImage, por lo que reutilizamos el dto e invertimos los roles
        this.senderId= request.receiverId();
        labelName.setText(request.receiverName());
        if(request.receiverImage() != null){
            Image pic = new Image(request.receiverImage());
            imgAvatar.setImage(pic);
        }
    }

    @FXML
    private void clickOnProfilePic(){
        FachadaGUI.getInstance().mostrarPantallaCarga();
        cMenu.clickOnOtherProfile(senderId, userEmail);
    }
    @FXML
    private void clickOnUsername(){
        FachadaGUI.getInstance().mostrarPantallaCarga();
        cMenu.clickOnOtherProfile(senderId, userEmail);
    }

    @FXML
    private void clickOnAccept(){
        try{
            FriendApi.acceptRequest(request.id());

            Platform.runLater(() -> removeFromUI());

        } catch (Exception e) {e.printStackTrace();}
    }
    @FXML
    private void clickOnReject(){
        try{
            FriendApi.rejectRequest(request.id());

            Platform.runLater(() -> removeFromUI());

        } catch (Exception e) {e.printStackTrace();}
    }

    private void removeFromUI() {
        Parent parent = root.getParent();
        if (parent instanceof VBox vbox) {
            vbox.getChildren().remove(root);
        }
    }


}

