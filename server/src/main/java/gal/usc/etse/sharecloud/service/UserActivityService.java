package gal.usc.etse.sharecloud.service;

import gal.usc.etse.sharecloud.model.ActivityPayload;
import gal.usc.etse.sharecloud.model.ActivityType;
import gal.usc.etse.sharecloud.model.ListenedTrackPayload;
import gal.usc.etse.sharecloud.model.entity.TrackInfo;
import gal.usc.etse.sharecloud.model.entity.UserActivity;
import gal.usc.etse.sharecloud.repository.UserActivityRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserActivityService {
    private final UserActivityRepository activityRepo;


    public UserActivityService(UserActivityRepository activityRepo) {
        this.activityRepo = activityRepo;
    }

    // Se actualizan las ultimas canciones escuchadas contenidas en 'ListenedTrackPayload' en la BD
    public void updateListenedTrackState(String userId, List<TrackInfo> tracks) {
        UserActivity activity = activityRepo.findByUserIdAndType(userId, ActivityType.LISTENED_TRACK)
                .orElseGet(() -> {
                    UserActivity a = new UserActivity();
                    a.setUserId(userId);
                    a.setType(ActivityType.LISTENED_TRACK);
                    return a;
                });

        ListenedTrackPayload payload = new ListenedTrackPayload();
        payload.setTracks(tracks);

        activity.setPayload(payload);
        activity.setUpdatedAt(Instant.now());

        activityRepo.save(activity);
    }

}
