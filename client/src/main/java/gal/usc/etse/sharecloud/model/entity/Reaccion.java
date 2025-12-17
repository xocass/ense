package gal.usc.etse.sharecloud.model.entity;



import java.time.Instant;

public class Reaccion {
    private String id;

    private String senderId;
    private String receiverId;

    private ReactionType reactionType;

    private String trackName;

    private String comentario;

    private Instant createdAt;
    private Instant expiresAt;
}
