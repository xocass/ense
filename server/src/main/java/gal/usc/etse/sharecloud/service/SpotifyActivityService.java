package gal.usc.etse.sharecloud.service;

import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyArtist;
import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyTrack;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopArtistsResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopTracksResponse;
import gal.usc.etse.sharecloud.model.entity.*;
import gal.usc.etse.sharecloud.repository.UserActivityRepository;
import gal.usc.etse.sharecloud.repository.UserRepository;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

import static gal.usc.etse.sharecloud.model.entity.ActivityType.LISTENED_TRACKS;


@Service
public class SpotifyActivityService {
    private final SpotifyService spotifyService;
    private final UserRepository userRepository;
    private final UserActivityRepository userActivityRepository;


    public SpotifyActivityService(SpotifyService spotifyService, UserRepository userRepository, UserActivityRepository userActivityRepository) {
        this.spotifyService = spotifyService;
        this.userRepository = userRepository;
        this.userActivityRepository = userActivityRepository;
    }


    public UserActivity getUserActivity(String userId, ActivityType activityType) {
        return userActivityRepository
                .findByUserIdAndType(userId, activityType)
                .orElseThrow();
    }

    public void updateRecentlyPlayedTracks(String userId, int limit) throws Exception {
        SpotifyRecentlyPlayedResponse response = spotifyService.getRecentlyPlayed(userId, limit);

        List<TrackInfo> tracks = response.items().stream()
                .map(this::mapRecentlyPlayedItemToTrackInfo)
                .toList();

        ListenedTrackPayload payload = new ListenedTrackPayload();
        payload.setTracks(tracks);
        updateActivity(userId, LISTENED_TRACKS, payload);
    }

    public void updateTopTracks(String userId, int limit) throws Exception {
        SpotifyTopTracksResponse topTracksResponse = spotifyService.getTopTracks(userId, limit);
        List<TrackInfo> tracks = topTracksResponse.items().stream()
                .map(this::mapSpotifyTrackToTrackInfo)
                .toList();

        TopTracksPayload payload = new TopTracksPayload(limit, tracks);
        updateActivity(userId, ActivityType.TOP_TRACKS, payload);
    }

    public void updateTopArtists(String userId, int limit) throws Exception {
        SpotifyTopArtistsResponse topArtistsResponse =  spotifyService.getTopArtists(userId, limit);
        List<ArtistInfo> artists = mapTopArtistsToArtistInfo(topArtistsResponse);

        TopArtistsPayload payload = new TopArtistsPayload(limit, artists);
        updateActivity(userId, ActivityType.TOP_ARTISTS, payload);
    }

    private void updateActivity(String userId, ActivityType type, ActivityPayload payload) {
        UserActivity activity = userActivityRepository.findByUserIdAndType(userId, type)
                .orElseGet(UserActivity::new);
        activity.setUserId(userId);
        activity.setType(type);
        activity.setPayload(payload);
        activity.setUpdatedAt(Instant.now());

        userActivityRepository.save(activity);
    }

    public SpotifyRecentlyPlayedResponse returnListenedTrackState(String userId, int limitSave, int limitReturn) throws Exception {
        SpotifyRecentlyPlayedResponse response = spotifyService.getRecentlyPlayed(userId, limitReturn);

        if(limitSave>0){
            List<TrackInfo> tracksReturned = response.items().stream()
                    .map(this::mapRecentlyPlayedItemToTrackInfo)
                    .toList();
            List<TrackInfo> tracksSaved = tracksReturned.stream()
                    .limit(limitSave)
                    .toList();
            ListenedTrackPayload payload = new ListenedTrackPayload();
            payload.setTracks(tracksSaved);
            updateActivity(userId, LISTENED_TRACKS, payload);
        }
        return response;
    }

    public SpotifyTopTracksResponse returnTopTracks(String userId, int limit) throws Exception {
        return spotifyService.getTopTracks(userId, limit);
    }

    public SpotifyTopArtistsResponse returnTopArtists(String userId, int limit) throws Exception {
        return spotifyService.getTopArtists(userId, limit);
    }



    /* ##################
     *      MAPPERS
     * ################## */

    private TrackInfo mapSpotifyTrackToTrackInfo(SpotifyTrack track) {
        List<String> artists = track.artists().stream()
                .map(SpotifyArtist::name)
                .toList();
        String image = null;
        if (track.album() != null && track.album().images() != null && !track.album().images().isEmpty()) {
            image = track.album().images().get(0).url();
        }

        return new TrackInfo(
                track.id(),
                track.name(),
                artists,
                image,
                null,
                track.duration_ms()
        );
    }

    private TrackInfo mapRecentlyPlayedItemToTrackInfo(SpotifyRecentlyPlayedResponse.Item item) {
        TrackInfo track = mapSpotifyTrackToTrackInfo(item.track());
        track.setPlayedAt(item.playedAt());
        return track;
    }

    private List<ArtistInfo> mapTopArtistsToArtistInfo(SpotifyTopArtistsResponse dto) {
        return dto.items().stream()
                .map(item -> {

                    String image = null;
                    if (item.images() != null && !item.images().isEmpty()) {
                        image = item.images().get(0).url();
                    }

                    return new ArtistInfo(
                            item.id(),
                            item.name(),
                            image
                    );
                })
                .toList();
    }

}
