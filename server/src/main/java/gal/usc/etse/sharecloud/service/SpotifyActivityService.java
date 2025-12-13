package gal.usc.etse.sharecloud.service;

import gal.usc.etse.sharecloud.model.ListenedTrackPayload;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.entity.TrackInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpotifyActivityService {
    private final SpotifyService spotifyService;
    private final UserActivityService activityService;


    public SpotifyActivityService(SpotifyService spotifyService, UserActivityService activityService) {
        this.spotifyService = spotifyService;
        this.activityService = activityService;
    }


    // Se llama a obtener las $limit últimas canciones, se encapsulan, y se llama a guardar en la BD
    public void updateListenedTrackState(String userId, int limit) throws Exception {
        SpotifyRecentlyPlayedResponse response = spotifyService.getRecentlyPlayed(userId, limit);
        List<TrackInfo> tracks = new ArrayList<>();

        for (SpotifyRecentlyPlayedResponse.Item item : response.items()) {
            TrackInfo track = new TrackInfo();
            track.setTrackId(item.track().id());
            track.setTrackName(item.track().name());
            track.setArtistName(item.track().artists().get(0).name());
            track.setImageUrl(item.track().album().images().get(0).url());
            track.setPlayedAt(item.playedAt());

            tracks.add(track);
        }

        activityService.upsertListenedTrackState(
                userId,
                tracks);
    }
}
