package gal.usc.etse.sharecloud.model.entity;

import java.util.List;

public class TopArtistsPayload implements ActivityPayload {
    private int limit;
    private List<ArtistInfo> artists;

    public TopArtistsPayload() {}

    public TopArtistsPayload(int limit, List<ArtistInfo> artists) {
        this.limit = limit;
        this.artists = artists;
    }

    public int getLimit() {return limit;}
    public List<ArtistInfo> getArtists() {return artists;}

    public void setLimit(int limit) {this.limit = limit;}
    public void setArtists(List<ArtistInfo> artists) {this.artists = artists;}
}
