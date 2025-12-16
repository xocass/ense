package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.ShareCloudBoot;
import gal.usc.etse.sharecloud.http.FriendApi;
import gal.usc.etse.sharecloud.model.dto.FriendRequest;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class cPopUpNotification {
    @FXML VBox vboxNotifications;
    @FXML Button btnClose;


    @FXML
    private void clickOnClose() { ((Stage) btnClose.getScene().getWindow()).close();}

    @FXML
    private void initialize() {
        vboxNotifications.getChildren().clear();

        new Thread(() -> {
            try {
                List<FriendRequest> requests = FriendApi.getPendingRequests();

                Platform.runLater(() -> {
                    for (FriendRequest req : requests) {
                        addRequestItem(req);
                    }
                });

            } catch (Exception e) {e.printStackTrace();}
        }).start();
    }

    private void addRequestItem(FriendRequest request) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/templateFriendRequest.fxml")
            );
            HBox item = loader.load();
            item.setMaxWidth(Double.MAX_VALUE);

            cFriendRequestItem controller = loader.getController();
            controller.setRequest(request);

            vboxNotifications.getChildren().add(item);

        } catch (IOException e) {e.printStackTrace();}
    }
}
