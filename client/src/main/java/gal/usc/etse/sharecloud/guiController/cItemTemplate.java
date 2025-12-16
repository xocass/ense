package gal.usc.etse.sharecloud.guiController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

public class cItemTemplate {
    @FXML private Label name;
    @FXML private Label artist;
    @FXML private ImageView image;
    @FXML private Label rank;
    @FXML private VBox root;

    public void setName(String name) {
        this.name.setText(name);
    }
    public void setArtist(String artist) {
        this.artist.setText(artist);
    }
    public void setImage(Image image) {
        this.image.setImage(image);
    }
    public void setRank(String rank) {
        this.rank.setText(rank);
    }

    //funcion para borrar el segundo label
    public void invLabel(){
        artist.setVisible(false);
    }

    @FXML
    private void onMouseEntered() {
        root.setStyle("""
            -fx-background-color: #1db95433;
            -fx-background-radius: 8;
        """);
    }
    @FXML
    private void onMouseExited() {
        root.setStyle("""
            -fx-background-color: transparent;
        """);
    }
}
