package gal.usc.etse.sharecloud.model.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedItem {
    private String id;
    private TrackInfo track;
    private SpotifyProfile spotifyProfile;

    @JsonCreator
    public FeedItem(
            @JsonProperty("id") String id,
            @JsonProperty("spotifyProfile") SpotifyProfile spotifyProfile,
            @JsonProperty("track") TrackInfo track
    ) {
        this.id = id;
        this.spotifyProfile = spotifyProfile;
        this.track = track;
    }

    public String getId() {return id;}
    public TrackInfo getTrack() {return track;}
    public SpotifyProfile getSpotifyProfile() {return spotifyProfile;}
}
