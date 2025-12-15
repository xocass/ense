package gal.usc.etse.sharecloud.model.dto;

import java.time.Instant;

public record FriendRequest(String id,
                            String senderId,
                            String senderName,
                            String senderImage,
                            Instant createdAt
) {}
