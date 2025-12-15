package gal.usc.etse.sharecloud.guiController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class cTemplateRecentTrack {
    @FXML
    private ImageView image;
    @FXML
    private Label name;
    @FXML
    private Label artists;
    @FXML
    private Label duration;
    @FXML
    private ImageView preview;

    private String previewURL=null;

    public void setImage(Image image) {
        this.image.setImage(image);
    }
    public void setName(String name) {
        this.name.setText(name);
    }
    public void setArtist(String artist) {
        this.artists.setText(artist);
    }
    public void setDuration(Integer duration) {
        long totalSeconds = duration / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        this.duration.setText(String.format("%02d:%02d", minutes, seconds));
    }
    public void setPreview(String previewURL) {
        if (previewURL != null) {
            this.preview.setImage(new Image(getClass().getResource(
                    "/gal/usc/etse/sharecloud/imgs/play.png"
            ).toExternalForm()));
            this.previewURL = previewURL;

        }
    }

    @FXML
    public void play(){
        if (previewURL != null) {
            Media media = new Media(previewURL);
            MediaPlayer player = new MediaPlayer(media);
            player.play();
        }
    }
}
