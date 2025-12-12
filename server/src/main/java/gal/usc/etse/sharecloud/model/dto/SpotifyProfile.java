package gal.usc.etse.sharecloud.model.dto;

import java.util.Date;

public record SpotifyProfile(
        String spotifyId,
        String displayName,
        String email,
        String country,
        String image
) {}
