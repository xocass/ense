package gal.usc.etse.sharecloud.guiController;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class cLoading {
    @FXML private ImageView spinner;
    @FXML private Label labelLoading;

    private RotateTransition rotate;


    @FXML
    public void initialize(){
        rotate = new RotateTransition(Duration.seconds(0.8), spinner);
        rotate.setByAngle(-360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setInterpolator(javafx.animation.Interpolator.LINEAR);
        rotate.play();
    }
}
