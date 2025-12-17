package gal.usc.etse.sharecloud.model.dto;

public record Like(String id,
                   String senderId,
                   String receiverId,
                   String trackName,
                   String senderName,
                   String senderImage) {

    public static Like from(gal.usc.etse.sharecloud.model.entity.Like like) {
        return new Like(like.getId(),like.getSenderId(),like.getReceiverId(),like.getTrackName(),like.getSenderName(),like.getSenderImage());
    }
}
