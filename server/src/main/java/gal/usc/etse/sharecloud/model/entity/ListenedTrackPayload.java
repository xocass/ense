package gal.usc.etse.sharecloud.model.entity;

import java.util.ArrayList;
import java.util.List;

public class ListenedTrackPayload implements ActivityPayload {
    private List<TrackInfo> tracks;


    public ListenedTrackPayload(){this.tracks=new ArrayList<>();}


    public List<TrackInfo> getTracks() {return tracks;}
    public void setTracks(List<TrackInfo> tracks) {this.tracks = tracks;}

}
