package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.model.dto.UserSearchResult;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class cUserSearchResult {
    private UserSearchResult userSearchResult;

    @FXML ImageView profilePic;
    @FXML Label labelUsername;

    public UserSearchResult getUserSearchResult() {return userSearchResult;}
    public void setUserSearchResult(UserSearchResult userSearchResult) {this.userSearchResult = userSearchResult;}

    public void setLabelUsername(String username) {this.labelUsername.setText(username);}
    public ImageView getProfilePic() {return profilePic;}
}
