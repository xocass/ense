package gal.usc.etse.sharecloud.repository;

import gal.usc.etse.sharecloud.model.entity.FriendRequest;
import gal.usc.etse.sharecloud.model.entity.FriendRequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {
    boolean existsBySenderIdAndReceiverIdAndStatus(String senderId, String receiverId, FriendRequestStatus status);
    List<FriendRequest> findByReceiverIdAndStatus(String receiverId, FriendRequestStatus status);
    Optional<FriendRequest> findBySenderIdAndReceiverIdAndStatus(String senderId, String receiverId, FriendRequestStatus status);
}

