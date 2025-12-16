package gal.usc.etse.sharecloud.model.entity;

import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopArtistsResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopTracksResponse;

public class SpotifyResponseCompact {
    private SpotifyProfile profileView;
    private SpotifyRecentlyPlayedResponse recentlyPlayed;
    private SpotifyTopTracksResponse topTracks;
    private SpotifyTopArtistsResponse topArtists;
    private Boolean isFollowing;

    public SpotifyResponseCompact(SpotifyProfile profileView, SpotifyRecentlyPlayedResponse recentlyPlayed,
                                  SpotifyTopTracksResponse topTracks, SpotifyTopArtistsResponse topArtists,
                                  Boolean isFollowing) {
        this.profileView = profileView;
        this.recentlyPlayed = recentlyPlayed;
        this.topTracks = topTracks;
        this.topArtists = topArtists;
        this.isFollowing = isFollowing;
    }

    public SpotifyProfile getProfileView() {return profileView;}
    public void setProfileView(SpotifyProfile profileView) {this.profileView = profileView;}
    public SpotifyRecentlyPlayedResponse getRecentlyPlayed() {return recentlyPlayed;}
    public void setRecentlyPlayed(SpotifyRecentlyPlayedResponse recentlyPlayed) {this.recentlyPlayed = recentlyPlayed;}
    public SpotifyTopTracksResponse getTopTracks() {return topTracks;}
    public void setTopTracks(SpotifyTopTracksResponse topTracks) {this.topTracks = topTracks;}
    public SpotifyTopArtistsResponse getTopArtists() {return topArtists;}
    public void setTopArtists(SpotifyTopArtistsResponse topArtists) {this.topArtists = topArtists;}
    public boolean isFollowing() {return isFollowing;}
    public void setFollowing(boolean isFollowing) {this.isFollowing = isFollowing;}
}
