package gal.usc.etse.sharecloud.model.dto;

import gal.usc.etse.sharecloud.model.entity.FriendRequestStatus;

import java.time.Instant;

public record FriendRequest(String id,
                            FriendRequestStatus status,
                            String senderId,
                            String receiverId,
                            String receiverName,
                            String receiverImage
) {}
