package gal.usc.etse.sharecloud.service;

import gal.usc.etse.sharecloud.model.dto.FeedItem;
import gal.usc.etse.sharecloud.model.entity.ActivityType;
import gal.usc.etse.sharecloud.model.entity.ListenedTrackPayload;
import gal.usc.etse.sharecloud.model.entity.TrackInfo;
import gal.usc.etse.sharecloud.model.entity.User;
import gal.usc.etse.sharecloud.repository.UserActivityRepository;
import gal.usc.etse.sharecloud.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class FeedService {

    private final UserRepository userRepo;
    private final UserActivityRepository activityRepo;

    public FeedService(UserRepository userRepo,
                       UserActivityRepository activityRepo) {
        this.userRepo = userRepo;
        this.activityRepo = activityRepo;
    }

    public List<FeedItem> getFriendsFeed(String id) {

        User current = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> friendsIds = current.getFriendIds();
        if (friendsIds == null || friendsIds.isEmpty()) {
            return List.of();
        }

        List<FeedItem> feed = new ArrayList<>();

        for (String friendId : friendsIds) {

            User friend = userRepo.findById(friendId).orElse(null);
            if (friend == null) continue;

            activityRepo
                    .findByUserIdAndType(friendId, ActivityType.LISTENED_TRACKS)
                    .ifPresent(activity -> {

                        ListenedTrackPayload payload =
                                (ListenedTrackPayload) activity.getPayload();

                        if (payload == null || payload.getTracks() == null) return;

                        for (TrackInfo track : payload.getTracks()) {

                            feed.add(new FeedItem(
                                    friend.getId(),
                                    friend.getSpotifyProfile(),
                                    track
                            ));
                        }
                    });
        }

        // Orden cronológico (feed real)
        feed.sort(
                Comparator.comparing(
                        (FeedItem item) -> item.track().getPlayedAt()
                ).reversed()
        );

        return feed;
    }
}