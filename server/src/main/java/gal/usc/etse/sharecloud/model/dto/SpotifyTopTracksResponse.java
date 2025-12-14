package gal.usc.etse.sharecloud.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyTrack;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyTopTracksResponse(List<SpotifyTrack> items) {}