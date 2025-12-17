package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.ShareCloudBoot;
import gal.usc.etse.sharecloud.http.FeedApi;
import gal.usc.etse.sharecloud.http.FriendApi;
import gal.usc.etse.sharecloud.model.dto.FriendRequest;
import gal.usc.etse.sharecloud.model.dto.Like;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class cPopUpNotification {
    @FXML VBox vboxNotifications;
    @FXML Button btnClose;

    private String userEmail;


    public void setUserEmail(String userEmail){this.userEmail=userEmail;}


    @FXML
    private void clickOnClose() { ((Stage) btnClose.getScene().getWindow()).close();}

    @FXML
    private void initialize() {
        vboxNotifications.getChildren().clear();

        new Thread(() -> {
            try {
                List<FriendRequest> requests = FriendApi.getPendingRequests();
                List<FriendRequest> notifications = FriendApi.getRequestVisibleNotifications();
                List<Like> likes = FeedApi.getLikes();

                Platform.runLater(() -> {
                    if (notifications.isEmpty() && requests.isEmpty() && likes.isEmpty()) {
                        Label empty = new Label("No tienes nuevas notificaciones");
                        empty.getStyleClass().add("side-empty-text");
                        vboxNotifications.getChildren().add(empty);
                        return;
                    }

                    for (FriendRequest req : requests) {
                        addRequestItem(req);
                    }
                    for (FriendRequest notif : notifications) {
                        addInfoItem(notif);
                    }
                    for (Like like : likes) {
                        addInfoItem(like);
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
            controller.setEmail(userEmail);
            controller.setRequest(request);

            vboxNotifications.getChildren().add(item);

        } catch (IOException e) {e.printStackTrace();}
    }

    private void addInfoItem(FriendRequest notification) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/templateFriendInfo.fxml")
            );
            HBox item = loader.load();
            item.setMaxWidth(Double.MAX_VALUE);

            cFriendInfoItem controller = loader.getController();
            controller.setEmail(userEmail);
            controller.setNotification(notification, vboxNotifications, item);

            vboxNotifications.getChildren().add(item);

        } catch (IOException e) {e.printStackTrace();}
    }

    private void addInfoItem(Like like) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/templateFriendInfo.fxml")
            );
            HBox item = loader.load();
            item.setMaxWidth(Double.MAX_VALUE);

            cFriendInfoItem controller = loader.getController();
            controller.setEmail(userEmail);
            controller.setNotification(like, vboxNotifications, item);

            vboxNotifications.getChildren().add(item);

        } catch (IOException e) {e.printStackTrace();}
    }
}
