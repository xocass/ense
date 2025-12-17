package gal.usc.etse.sharecloud.model.dto;

public record Like(String id,
                   String senderId,
                   String receiverId,
                   String trackName,
                   String senderName,
                   String senderImage
) {
}
