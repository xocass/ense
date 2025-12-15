package gal.usc.etse.sharecloud.service;

import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyAlbum;
import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyArtist;
import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyImage;
import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyTrack;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopArtistsResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopTracksResponse;
import gal.usc.etse.sharecloud.model.entity.*;
import gal.usc.etse.sharecloud.repository.UserActivityRepository;
import gal.usc.etse.sharecloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static gal.usc.etse.sharecloud.model.entity.ActivityType.*;

@Service
public class UserActivityService {
    private final UserActivityRepository userActivityRepository;

    @Autowired
    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public SpotifyRecentlyPlayedResponse returnOtherRecentlyPlayed(String userId) throws Exception{
        UserActivity activity = userActivityRepository.findByUserIdAndType(userId,LISTENED_TRACKS).orElseThrow(() -> new Exception("Recently played not reachable at database"));
        ListenedTrackPayload payload = (ListenedTrackPayload) activity.getPayload();
        List<SpotifyRecentlyPlayedResponse.Item> items =
                payload.getTracks().stream()
                        .map(trackInfo -> {

                            // Artistas: List<String> → List<SpotifyArtist>
                            List<SpotifyArtist> artists =
                                    trackInfo.getArtists().stream()
                                            .map(SpotifyArtist::new)
                                            .toList();

                            // Álbum con imagen
                            SpotifyAlbum album = new SpotifyAlbum(
                                    List.of(new SpotifyImage(trackInfo.getImageUrl()))
                            );

                            // Track
                            SpotifyTrack track = new SpotifyTrack(
                                    trackInfo.getTrackId(),
                                    trackInfo.getTrackName(),
                                    artists,
                                    album,
                                    trackInfo.getDuration_ms()
                            );

                            // Item final
                            return new SpotifyRecentlyPlayedResponse.Item(
                                    trackInfo.getPlayedAt(),
                                    track
                            );
                        })
                        .toList();

        return new SpotifyRecentlyPlayedResponse(items);
    }
    public SpotifyTopTracksResponse returnOtherTopTracks(String userId) throws Exception{
        UserActivity activity = userActivityRepository
                .findByUserIdAndType(userId, TOP_TRACKS)
                .orElseThrow(() -> new Exception("Top tracks not reachable at database"));

        TopTracksPayload payload = (TopTracksPayload) activity.getPayload();

        List<SpotifyTrack> tracks =
                payload.getTracks().stream()
                        .map(trackInfo -> {

                            // Artistas: List<String> → List<SpotifyArtist>
                            List<SpotifyArtist> artists =
                                    trackInfo.getArtists().stream()
                                            .map(SpotifyArtist::new)
                                            .toList();

                            // Álbum con imagen
                            SpotifyAlbum album = new SpotifyAlbum(
                                    List.of(new SpotifyImage(trackInfo.getImageUrl()))
                            );

                            // Track final
                            return new SpotifyTrack(
                                    trackInfo.getTrackId(),
                                    trackInfo.getTrackName(),
                                    artists,
                                    album,
                                    trackInfo.getDuration_ms()
                            );
                        })
                        .toList();

        return new SpotifyTopTracksResponse(tracks);
    }
    public SpotifyTopArtistsResponse returnOtherTopArtists(String userId) throws Exception {
        UserActivity activity = userActivityRepository
                .findByUserIdAndType(userId, TOP_ARTISTS)
                .orElseThrow(() -> new Exception("Top artists not reachable at database"));

        if (!(activity.getPayload() instanceof TopArtistsPayload payload)) {
            throw new IllegalStateException("Invalid payload type for TOP_ARTISTS");
        }

        List<SpotifyTopArtistsResponse.Item> items =
                payload.getArtists().stream()
                        .map(artistInfo -> {

                            List<SpotifyTopArtistsResponse.Image> images =
                                    (artistInfo.getImage() != null && !artistInfo.getImage().isBlank())
                                            ? List.of(new SpotifyTopArtistsResponse.Image(artistInfo.getImage()))
                                            : List.of();

                            return new SpotifyTopArtistsResponse.Item(
                                    artistInfo.getSpotifyId(),
                                    artistInfo.getName(),
                                    images
                            );
                        })
                        .toList();

        return new SpotifyTopArtistsResponse(items);
    }

}
