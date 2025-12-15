package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.model.dto.UserSearchResult;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class cUserItem {
    @FXML private ImageView profilePic;
    @FXML private Label labelName;
    @FXML private Label labelCountry;
    @FXML private ImageView iconOptions;

    private String userId;
    private boolean isFriend;


    @FXML
    private void initialize() {
        // click en el item al completo
        profilePic.getParent().setOnMouseClicked(e -> onItemClicked());
    }
    public void setLabelName(String name) {labelName.setText(name);}
    public void setLabelCountry(String country) {labelCountry.setText(country);}
    public ImageView getProfilePic() {return profilePic;}

    public void setUserId(String userId) {this.userId = userId;}
    public void setIsFriend(boolean isFriend) {this.isFriend = isFriend;}
    public String getUserId() {return userId;}
    public boolean getIsFriend() {return isFriend;}

    public void setUser(UserSearchResult user, Boolean isFriend){
        this.userId = user.id();
        this.isFriend = isFriend;

        labelName.setText(user.username());
        labelCountry.setText(user.country());

        Image pic = new Image(user.image());
        profilePic.setImage(pic);

        if (!isFriend) {
            /*iconOptions.setImage(
                    new Image(getClass().getResourceAsStream(
                            "/gal/usc/etse/sharecloud/imgs/icon-add.png"
                    ))
            );*/
        } else {
            iconOptions.setVisible(false);
            iconOptions.setManaged(false);
        }
    }

    private void onItemClicked() {
        System.out.println("Click en usuario: " + userId);
        // aquí:
        // - abrir perfil
        // - enviar solicitud
        // - aceptar
    }

}
