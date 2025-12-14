package gal.usc.etse.sharecloud.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyTrack;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyRecentlyPlayedResponse(List<Item> items) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            @JsonProperty("played_at") Instant playedAt,
            SpotifyTrack track
    ) {}
}

