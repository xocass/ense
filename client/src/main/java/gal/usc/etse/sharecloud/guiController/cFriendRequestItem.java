package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.http.FriendApi;
import gal.usc.etse.sharecloud.model.dto.FriendRequest;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class cFriendRequestItem {
    @FXML private ImageView imgAvatar;
    @FXML private Label labelName;
    @FXML private Button btnAccept;
    @FXML private Button btnReject;

    private FriendRequest request;

    public void setRequest(FriendRequest request) {
        this.request = request;

        labelName.setText(request.senderName());
        if(request.senderImage() != null){
            Image pic = new Image(request.senderImage());
            imgAvatar.setImage(pic);
        }
    }

    @FXML
    private void clickOnProfilePic(){

    }
    @FXML
    private void clickOnUsername(){

    }
    @FXML
    private void clickOnAccept(){
        try{
            FriendApi.acceptRequest(request.id());

        } catch (Exception e) {e.printStackTrace();}
    }
    @FXML
    private void clickOnReject(){
        try{
            FriendApi.rejectRequest(request.id());

        } catch (Exception e) {e.printStackTrace();}
    }

}

