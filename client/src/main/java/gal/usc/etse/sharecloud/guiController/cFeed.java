package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class cFeed {
    private String userEmail;

    public void setEmail(String email) {this.userEmail = email;}












    /*  ###################################################################  */
    /*  ########  FUNCIONALIDADES E INTERACIONES CON MENU LATERAL  ########  */
    /*  ###################################################################  */
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

    @FXML
    public void initialize() {
        cMenu.activarAmigos(btnFriends, btnSearch, friendsPane, searchPane);
        cMenu.cargarAmigos(vboxFriends, userEmail);
        cMenu.configurarBusqueda(fieldSearch, vboxResults, userEmail);
        cMenu.configurarNotificaciones(btnNotification);
    }
    @FXML
    private void clickViewProfile(){
        FachadaGUI.getInstance().mostrarPantallaCarga();
        cMenu.clickOnUserProfile(userEmail);
    }
    @FXML
    private void clickOnNotification(){
        cMenu.abrirNotificaciones();
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
