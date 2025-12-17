package gal.usc.etse.sharecloud.model.entity;

import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopArtistsResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopTracksResponse;
import gal.usc.etse.sharecloud.model.dto.UserBooleans;

public class SpotifyResponseCompact {
    private SpotifyProfile profileView;
    private SpotifyRecentlyPlayedResponse recentlyPlayed;
    private SpotifyTopTracksResponse topTracks;
    private SpotifyTopArtistsResponse topArtists;
    private UserBooleans booleans;

    public SpotifyResponseCompact(SpotifyProfile profileView, SpotifyRecentlyPlayedResponse recentlyPlayed,
                                  SpotifyTopTracksResponse topTracks, SpotifyTopArtistsResponse topArtists,
                                  UserBooleans booleans) {
        this.profileView = profileView;
        this.recentlyPlayed = recentlyPlayed;
        this.topTracks = topTracks;
        this.topArtists = topArtists;
        this.booleans = booleans;
    }

    public SpotifyProfile getProfileView() {return profileView;}
    public void setProfileView(SpotifyProfile profileView) {this.profileView = profileView;}
    public SpotifyRecentlyPlayedResponse getRecentlyPlayed() {return recentlyPlayed;}
    public void setRecentlyPlayed(SpotifyRecentlyPlayedResponse recentlyPlayed) {this.recentlyPlayed = recentlyPlayed;}
    public SpotifyTopTracksResponse getTopTracks() {return topTracks;}
    public void setTopTracks(SpotifyTopTracksResponse topTracks) {this.topTracks = topTracks;}
    public SpotifyTopArtistsResponse getTopArtists() {return topArtists;}
    public void setTopArtists(SpotifyTopArtistsResponse topArtists) {this.topArtists = topArtists;}
    public UserBooleans getUserBooleans() {return booleans;}
    public void setUserBooleans(UserBooleans booleans) {this.booleans = booleans;}
}
