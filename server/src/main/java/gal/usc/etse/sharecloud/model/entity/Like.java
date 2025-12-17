package gal.usc.etse.sharecloud.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection ="likes")
public class Like {
    @Id
    private String id;

    private String senderId;
    private String receiverId;

    private String trackName;
    private String senderName;
    private String senderImage;

    @Indexed(expireAfter = "0s")
    private Instant expiresAt;

    public void setSenderId(String senderId) {this.senderId = senderId;}
    public void setReceiverId(String receiverId) {this.receiverId = receiverId;}
    public void setTrackName(String trackName) {this.trackName = trackName;}
    public void setExpiresAt(Instant expiresAt) {this.expiresAt = expiresAt;}
    public void setSenderName(String senderName) {this.senderName = senderName;}
    public void setSenderImage(String senderImage) {this.senderImage = senderImage;}

    public String getId() {return id;}
    public String getSenderId() {return senderId;}
    public String getReceiverId() {return receiverId;}
    public String getTrackName() {return trackName;}
    public String getSenderName() {return senderName;}
    public String getSenderImage() {return senderImage;}
}
