package gal.usc.etse.sharecloud.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "friend_requests")
public class FriendRequest {
    @Id
    private String id;

    private String senderId;
    private String receiverId;

    private FriendRequestStatus status;
    private boolean visibleForSender;
    private boolean visibleForReceiver;

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getSenderId() {return senderId;}
    public void setSenderId(String senderId) {this.senderId = senderId;}
    public String getReceiverId() {return receiverId;}
    public void setReceiverId(String receiverId) {this.receiverId = receiverId;}
    public FriendRequestStatus getStatus() {return status;}
    public void setStatus(FriendRequestStatus status) {this.status = status;}
    public boolean isVisibleForSender() {return visibleForSender;}
    public void setVisibleForSender(boolean visibleForSender) {this.visibleForSender = visibleForSender;}
    public boolean isVisibleForReceiver() {return visibleForReceiver;}
    public void setVisibleForReceiver(boolean visibleForReceiver) {this.visibleForReceiver = visibleForReceiver;}

}
