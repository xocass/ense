package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.model.dto.UserSearchResult;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class cUserSearchResult {
    private UserSearchResult userSearchResult;

    @FXML ImageView profilePic;
    @FXML Label labelUsername;
    private String id;

    public UserSearchResult getUserSearchResult() {return userSearchResult;}
    public void setUserSearchResult(UserSearchResult userSearchResult) {this.userSearchResult = userSearchResult;}
    public void setId(String id) {this.id = id;}

    public void setLabelUsername(String username) {this.labelUsername.setText(username);}
    public ImageView getProfilePic() {return profilePic;}
    public String getId() {return id;}
}
