package gal.usc.etse.sharecloud.model.entity;

import java.time.Instant;

public class TrackInfo {
    private String trackId;
    private String trackName;
    private String artistName;
    private String imageUrl;
    private Instant playedAt;

    public String getTrackId() {return trackId;};
    public String getTrackName() {return trackName;}
    public String getArtistName() {return artistName;}
    public String getImageUrl() {return imageUrl;}
    public Instant getPlayedAt() {return playedAt;}

    public void setTrackId(String trackId) {this.trackId = trackId;}
    public void setTrackName(String trackName) {this.trackName = trackName;}
    public void setArtistName(String artistName) {this.artistName = artistName;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
    public void setPlayedAt(Instant playedAt) {this.playedAt = playedAt;}
}
