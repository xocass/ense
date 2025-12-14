package gal.usc.etse.sharecloud.model.dto.SpotifyItems;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyTrack(String id,
                           String name,
                           List<SpotifyArtist> artists,
                           SpotifyAlbum album
) {}
