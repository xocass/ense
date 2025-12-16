package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.FriendApi;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

public class cUserItem {
    @FXML private ImageView profilePic;
    @FXML private Label labelName;
    @FXML private Label labelCountry;
    @FXML private HBox hboxItem;

    private String otherId;
    private String userEmail;
    private boolean isFriend;


    @FXML
    private void initialize() {
        hboxItem.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showContextMenu();
            }
        });
    }
    public void setOtherLabelName(String name) {labelName.setText(name);}
    public void setOtherLabelCountry(String country) {labelCountry.setText(country);}
    public ImageView getOtherProfilePic() {return profilePic;}

    public void setUserEmail(String userEmail) {this.userEmail = userEmail;}
    public void setOtherId(String userId) {this.otherId = userId;}
    public void setOtherIsFriend(boolean isFriend) {this.isFriend = isFriend;}
    public String getOtherId() {return otherId;}
    public boolean getOtherIsFriend() {return isFriend;}


    private void showContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem viewProfile = new MenuItem("Ver perfil");
        MenuItem addFriend = new MenuItem("Solicitar amistad");

        viewProfile.setOnAction(e1 -> clickOnOtherProfile());
        addFriend.setOnAction(e2 -> {
                try{
                    FriendApi.sendRequest(otherId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }});

        menu.getItems().addAll(viewProfile, addFriend);

        // Mostrar a la derecha del item
        Bounds bounds = hboxItem.localToScreen(hboxItem.getBoundsInLocal());
        menu.show(
                hboxItem,
                bounds.getMaxX(),
                bounds.getMinY()
        );
    }
    private void clickOnOtherProfile(){
        FachadaGUI.getInstance().mostrarPantallaCarga();
        cMenu.clickOnOtherProfile(otherId, userEmail);
    }

}
