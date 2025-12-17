package gal.usc.etse.sharecloud.service;

import gal.usc.etse.sharecloud.model.dto.UserSearchResult;
import gal.usc.etse.sharecloud.model.entity.FriendRequest;
import gal.usc.etse.sharecloud.model.entity.FriendRequestStatus;
import gal.usc.etse.sharecloud.model.entity.User;
import gal.usc.etse.sharecloud.repository.FriendRequestRepository;
import gal.usc.etse.sharecloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Autowired
    public FriendService(UserRepository userRepository, FriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
    }



    public void sendFriendRequest(String senderId, String receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("No puedes enviarte solicitud a ti mismo");
        }

        User sender = userRepository.findById(senderId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        if (sender.getFriendIds().contains(receiverId)) {
            throw new IllegalStateException("Ya sois amigos");
        }

        boolean alreadyExistsRequest = friendRequestRepository.existsBySenderIdAndReceiverIdAndStatus(
                senderId, receiverId, FriendRequestStatus.PENDING);

        if (alreadyExistsRequest) {
            throw new IllegalStateException("Solicitud ya enviada");
        }

        FriendRequest request = new FriendRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setStatus(FriendRequestStatus.PENDING);
        request.setVisibleForSender(true);
        request.setVisibleForReceiver(true);

        friendRequestRepository.save(request);
    }

    public List<gal.usc.etse.sharecloud.model.dto.FriendRequest> getPendingRequests(String receiverId) {
        List<FriendRequest> requests = friendRequestRepository.findByReceiverIdAndStatus(
                receiverId, FriendRequestStatus.PENDING);
        return requests.stream().map(req -> {
                    User sender = userRepository.findById(req.getSenderId()).orElseThrow(() ->
                                    new IllegalStateException("Sender not found: " + req.getSenderId()));
                    String senderName = null;
                    String senderImage = null;

                    if (sender.getSpotifyProfile() != null) {
                        senderName = sender.getSpotifyProfile().getDisplayName();
                        senderImage = sender.getSpotifyProfile().getImage();
                    }

                    return new gal.usc.etse.sharecloud.model.dto.FriendRequest(req.getId(), FriendRequestStatus.PENDING,
                            req.getReceiverId(), req.getSenderId(), senderName, senderImage);}).toList();
    }

    public List<gal.usc.etse.sharecloud.model.dto.FriendRequest> getAllRequestsVisible(String senderId) {
        List<FriendRequest> requests = friendRequestRepository.findBySenderIdAndVisibleForSender(senderId, true);
        return requests.stream().map(req -> {
            User receiver = userRepository.findById(req.getReceiverId()).orElseThrow(() ->
                    new IllegalStateException("Receiver not found: " + req.getReceiverId()));
            String receiverName = null;
            String receiverImage = null;

            if (receiver.getSpotifyProfile() != null) {
                receiverName = receiver.getSpotifyProfile().getDisplayName();
                receiverImage = receiver.getSpotifyProfile().getImage();
            }

            return new gal.usc.etse.sharecloud.model.dto.FriendRequest(req.getId(), req.getStatus(), senderId,
                    receiver.getId(), receiverName, receiverImage);}).toList();
    }

    public void senderSawFriendRequest(String requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId).orElseThrow(()->
                new IllegalStateException("Request not found: " + requestId));

        // false && false -> receiver ha aceptado/rechazado y sender ha cerrado la notificacion
        if(!request.isVisibleForReceiver() && !request.isVisibleForSender()) {
            friendRequestRepository.delete(request);
        }else{
            request.setVisibleForSender(false);
            friendRequestRepository.save(request);
        }
    }

    public void acceptRequest(String requestId, String userId) {
        FriendRequest request = friendRequestRepository.findById(requestId).orElseThrow();

        if (!request.getReceiverId().equals(userId)) {
            throw new SecurityException("No autorizado");
        }

        request.setStatus(FriendRequestStatus.ACCEPTED);
        request.setVisibleForSender(true);
        request.setVisibleForReceiver(false);
        friendRequestRepository.save(request);

        User sender = userRepository.findById(request.getSenderId()).orElseThrow();
        User receiver = userRepository.findById(request.getReceiverId()).orElseThrow();

        sender.getFriendIds().add(receiver.getId());
        receiver.getFriendIds().add(sender.getId());

        userRepository.save(sender);
        userRepository.save(receiver);
    }

    public void rejectRequest(String requestId, String userId) {
        FriendRequest request = friendRequestRepository.findById(requestId).orElseThrow();

        if (!request.getReceiverId().equals(userId)) {
            throw new SecurityException("No autorizado");
        }

        request.setStatus(FriendRequestStatus.REJECTED);
        request.setVisibleForSender(true);
        request.setVisibleForReceiver(false);
        friendRequestRepository.save(request);
    }

    public List<UserSearchResult> getFriends(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId));

        if (user.getFriendIds() == null || user.getFriendIds().isEmpty()) {
            return List.of();
        }

        List<User> friends = userRepository.findAllById(user.getFriendIds());

        return friends.stream()
                .map(friend -> new UserSearchResult(
                        friend.getId(),
                        friend.getSpotifyProfile().getDisplayName(),
                        friend.getSpotifyProfile().getImage(),
                        friend.getSpotifyProfile().getCountry(),
                        true
                ))
                .toList();

    }

    public void removeFriend(String currentUserId, String targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("No puedes eliminarte a ti mismo");
        }

        User current = userRepository.findById(currentUserId).orElseThrow(() -> new IllegalStateException("User not found"));
        User target = userRepository.findById(targetUserId).orElseThrow(() -> new IllegalStateException("Target user not found"));

        current.getFriendIds().remove(targetUserId);
        target.getFriendIds().remove(currentUserId);
        userRepository.save(current);
        userRepository.save(target);

        List<FriendRequest> requests = friendRequestRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
                currentUserId, targetUserId,
                targetUserId, currentUserId);

        if (!requests.isEmpty()) {
            friendRequestRepository.deleteAll(requests);
        }
    }
}
