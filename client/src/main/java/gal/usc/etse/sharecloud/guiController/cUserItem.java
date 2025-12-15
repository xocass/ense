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

    private String userId;
    private boolean isFriend;
    private FachadaGUI fgui;


    @FXML
    private void initialize() {
        hboxItem.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showContextMenu();
            }
        });
    }
    public void setFachadas(FachadaGUI fgui) {this.fgui = fgui;}
    public void setLabelName(String name) {labelName.setText(name);}
    public void setLabelCountry(String country) {labelCountry.setText(country);}
    public ImageView getProfilePic() {return profilePic;}

    public void setUserId(String userId) {this.userId = userId;}
    public void setIsFriend(boolean isFriend) {this.isFriend = isFriend;}
    public String getUserId() {return userId;}
    public boolean getIsFriend() {return isFriend;}


    private void showContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem viewProfile = new MenuItem("Ver perfil");
        MenuItem addFriend = new MenuItem("Solicitar amistad");

        //##############################################################
        //##############################################################
        viewProfile.setOnAction(e1 -> fgui.verCurrPerfil());
        addFriend.setOnAction(e2 -> {
                try{
                    FriendApi.sendRequest(userId);
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

}
