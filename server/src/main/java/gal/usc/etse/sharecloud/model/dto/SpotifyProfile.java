package gal.usc.etse.sharecloud.model.dto;

import java.util.Date;

public record SpotifyProfile(
        String spotifyID,
        String displayName,
        String email,
        String country,
        String image,
        Integer nFollowers,
        String profileURL
) {}
