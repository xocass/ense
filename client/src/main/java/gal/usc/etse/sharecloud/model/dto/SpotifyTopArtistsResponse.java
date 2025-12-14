package gal.usc.etse.sharecloud.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyTopArtistsResponse(List<Item> items) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String id,
            String name,
            List<Image> images
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Image(String url) {}
}