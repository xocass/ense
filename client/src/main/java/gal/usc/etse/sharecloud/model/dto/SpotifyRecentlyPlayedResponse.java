package gal.usc.etse.sharecloud.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyRecentlyPlayedResponse(List<Item> items) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            @JsonProperty("played_at") Instant playedAt,
            Track track
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Track(
            String id,
            String name,
            List<Artist> artists,
            Album album
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Artist(String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Album(List<Image> images) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Image(String url) {}
}
