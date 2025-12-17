package gal.usc.etse.sharecloud.model.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public class TrackInfo {
    private String trackId;
    private String trackName;
    private List<String> artists;
    private String imageUrl;
    private Instant playedAt;
    private Integer duration_ms;

    @JsonCreator
    public TrackInfo(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("artists") List<String> artists,
            @JsonProperty("imageUrl") String imageUrl,
            @JsonProperty("playedAt") Instant playedAt
    ) {
        this.trackId = id;
        this.trackName = name;
        this.artists = artists;
        this.imageUrl = imageUrl;
        this.playedAt = playedAt;
    }

    public String getTrackId() {return trackId;};
    public String getTrackName() {return trackName;}
    public List<String> getArtists() {return artists;}
    public String getImageUrl() {return imageUrl;}
    public Instant getPlayedAt() {return playedAt;}
    public Integer getDuration_ms() {return duration_ms;}

    public void setTrackId(String trackId) {this.trackId = trackId;}
    public void setTrackName(String trackName) {this.trackName = trackName;}
    public void setArtists(List<String> artists) {this.artists = artists;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
    public void setPlayedAt(Instant playedAt) {this.playedAt = playedAt;}
    public void setDuration_ms(Integer duration_ms) {this.duration_ms = duration_ms;}
}
