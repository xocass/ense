package gal.usc.etse.sharecloud.model.entity;

import java.util.List;

public class TopTracksPayload implements ActivityPayload {
    private int limit;
    private List<TrackInfo> tracks;

    public TopTracksPayload() {}

    public TopTracksPayload(int limit, List<TrackInfo> tracks) {
        this.limit = limit;
        this.tracks = tracks;
    }

    public int getLimit() {return limit;}
    public List<TrackInfo> getTracks() {return tracks;}

    public void setLimit(int limit) {this.limit = limit;}
    public void setTracks(List<TrackInfo> tracks) {this.tracks = tracks;}
}
