package gal.usc.etse.sharecloud.model.entity;

public class ArtistInfo {
    private String spotifyId;
    private String name;
    private String image;

    public ArtistInfo() {}

    public ArtistInfo(String spotifyId, String name, String image) {
        this.spotifyId = spotifyId;
        this.name = name;
        this.image = image;
    }

    public String getSpotifyId() {return spotifyId;}
    public String getName() {return name;}
    public String getImage() {return image;}

    public void setSpotifyId(String spotifyId) {this.spotifyId = spotifyId;}
    public void setName(String name) {this.name = name;}
    public void setImage(String image) {this.image = image;}
}
