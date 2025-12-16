package gal.usc.etse.sharecloud.model.dto;

import gal.usc.etse.sharecloud.model.entity.FriendRequestStatus;

public record FriendRequest(String id,
                            String senderId,
                            String senderName,
                            String senderImage
) {
}
