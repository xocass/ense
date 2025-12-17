package gal.usc.etse.sharecloud.service;

import gal.usc.etse.sharecloud.model.dto.FeedItem;
import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyArtist;
import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyTrack;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.entity.*;
import gal.usc.etse.sharecloud.repository.FeedRepository;
import gal.usc.etse.sharecloud.repository.UserActivityRepository;
import gal.usc.etse.sharecloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class FeedService {

    private final UserRepository userRepo;
    private final FeedRepository feedRepo;
    private final SpotifyService spotifyService;

    @Autowired
    public FeedService(UserRepository userRepo,
                       FeedRepository feedRepo,
                       SpotifyService spotifyService) {
        this.userRepo = userRepo;
        this.feedRepo = feedRepo;
        this.spotifyService = spotifyService;
    }

    public void updatePlayedToday(String id, SpotifyProfile spotifyProfile) throws Exception {
        SpotifyRecentlyPlayedResponse response = spotifyService.getPlayedToday(id);

        List<TrackInfo> tracks = response.items().stream()
                .map(this::mapRecentlyPlayedItemToTrackInfo)
                .toList();

        if(!tracks.isEmpty()) {
            feedRepo.save(new FeedCard(
                    id,
                    spotifyProfile,
                    tracks,
                    nextMidnight()
            ));
        }
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

            FeedCard actual = feedRepo.findById(friendId).orElse(null);
            if(actual == null) continue;

            if (actual.getTracks() == null || actual.getTracks().isEmpty()) {
                return List.of();
            }

            return actual.getTracks().stream()
                    .map(track -> new FeedItem(
                            actual.getId(),
                            actual.getSpotifyProfile(),
                            track
                    ))
                    .toList();
        }


        // Orden cronológico (feed real)
        feed.sort(
                Comparator.comparing(
                        (FeedItem item) -> item.track().getPlayedAt()
                ).reversed()
        );

        return feed;
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

    public static Instant nextMidnight() {
        ZoneId zone = ZoneId.systemDefault();

        return LocalDate.now(zone)
                .plusDays(1)
                .atStartOfDay(zone)
                .toInstant();
    }
}