package gal.usc.etse.sharecloud.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection ="reactions")
public class Reaccion {
    @Id
    private String id;

    private String senderId;
    private String receiverId;

    private ReactionType reactionType;

    private String trackName;

    private String comentario;

    private Instant createdAt;
    @Indexed(expireAfter = "0s")
    private Instant expiresAt;

    public void setSenderId(String senderId) {this.senderId = senderId;}
    public void setReceiverId(String receiverId) {this.receiverId = receiverId;}
    public void setReactionType(ReactionType reactionType) {this.reactionType = reactionType;}
    public void setTrackName(String trackName) {this.trackName = trackName;}
    public void setComentario(String comentario) {this.comentario = comentario;}
    public void setCreatedAt(Instant createdAt) {this.createdAt = createdAt;}
    public void setExpiresAt(Instant expiresAt) {this.expiresAt = expiresAt;}
}
