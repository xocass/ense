package gal.usc.etse.sharecloud.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gal.usc.etse.sharecloud.model.entity.TrackInfo;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;

public record FeedItem (
        @JsonProperty("id")
        String id,

        @JsonProperty("spotifyProfile")
        SpotifyProfile spotifyProfile,

        @JsonProperty("track")
        TrackInfo track
){
    @JsonCreator
    public FeedItem {}
}
