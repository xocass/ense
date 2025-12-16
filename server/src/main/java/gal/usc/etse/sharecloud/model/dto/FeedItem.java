package gal.usc.etse.sharecloud.model.dto;

import gal.usc.etse.sharecloud.model.entity.TrackInfo;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;

public record FeedItem (String id,
                        SpotifyProfile spotifyProfile,
                        TrackInfo track){
}
