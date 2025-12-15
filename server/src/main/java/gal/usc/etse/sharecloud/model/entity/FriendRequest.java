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

    private Instant createdAt;

}
