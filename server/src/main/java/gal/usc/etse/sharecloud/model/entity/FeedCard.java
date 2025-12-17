package gal.usc.etse.sharecloud.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "feed_cards")
public class FeedCard {

    @Id
    private String id;

    private SpotifyProfile spotifyProfile;
    private List<TrackInfo> tracks;


    @Indexed(expireAfter = "0s")
    private Instant expireAt;

    public FeedCard() {
    }

    public FeedCard(String id,
                    SpotifyProfile spotifyProfile,
                    List<TrackInfo> tracks,
                    Instant expireAt) {
        this.id = id;
        this.spotifyProfile = spotifyProfile;
        this.tracks = tracks;
        this.expireAt = expireAt;
    }

    public List<TrackInfo> getTracks() {
        return tracks;
    }
    public SpotifyProfile getSpotifyProfile() {
        return spotifyProfile;
    }
    public String getId(){
        return id;
    }
}
