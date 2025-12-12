package gal.usc.etse.sharecloud.model.entity;

public class SpotifyProfile {
    private String spotifyID;
    private String display_name;
    private String email;
    private String country;
    private String image;
    private Integer nFollowers;
    private String profileURL;


    public String getSpotifyID() {return spotifyID;}
    public String getDisplay_name() {return display_name;}
    public String getEmail() {return email;}
    public String getCountry() {return country;}
    public String getImage() {return image;}
    public Integer getnFollowers() {return nFollowers;}
    public String getProfileURL() {return profileURL;}

    public void setSpotifyID(String spotifyID) {this.spotifyID = spotifyID;}
    public void setDisplay_name(String display_name) {this.display_name = display_name;}
    public void setEmail(String email) {this.email = email;}
    public void setCountry(String country) {this.country = country;}
    public void setImage(String image) {this.image = image;}
    public void setnFollowers(Integer nFollowers) {this.nFollowers = nFollowers;}
    public void setProfileURL(String profileURL) {this.profileURL = profileURL;}
}
