package gal.usc.etse.sharecloud.model.entity;

import java.time.Instant;
import java.util.List;

public class TrackInfo {
    private String trackId;
    private String trackName;
    private List<String> artists;
    private String imageUrl;
    private Instant playedAt;

    public TrackInfo(String trackId, String trackName, List<String> artists, String imageUrl, Instant playedAt) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.artists = artists;
        this.imageUrl = imageUrl;
        this.playedAt = playedAt;
    }

    public String getTrackId() {return trackId;};
    public String getTrackName() {return trackName;}
    public List<String> getArtists() {return artists;}
    public String getImageUrl() {return imageUrl;}
    public Instant getPlayedAt() {return playedAt;}

    public void setTrackId(String trackId) {this.trackId = trackId;}
    public void setTrackName(String trackName) {this.trackName = trackName;}
    public void setArtists(List<String> artists) {this.artists = artists;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
    public void setPlayedAt(Instant playedAt) {this.playedAt = playedAt;}
}
