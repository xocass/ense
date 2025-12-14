package gal.usc.etse.sharecloud.model.dto.SpotifyItems;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyImage(String url) {}
