package gal.usc.etse.sharecloud.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpotifyProfile {

    @JsonProperty("spotifyID")
    private String spotifyId;

    private String displayName;
    private String email;
    private String country;
    private String profileURL;

    @JsonProperty("nFollowers")
    private Integer nFollowers;

    private String image;

    //getters
    public String getSpotifyId() {return spotifyId;}
    public String getDisplayName() {return displayName;}
    public String getEmail() {return email;}
    public String getCountry() {return country;}
    public String getProfileURL() {return profileURL;}
    public Integer getNFollowers() {return nFollowers;}
    public String getImage() {return image;}

    //setters
    public void setSpotifyId(String spotifyId) {this.spotifyId = spotifyId;}
    public void setDisplayName(String displayName) {this.displayName = displayName;}
    public void setEmail(String spotifyEmail) {this.email = spotifyEmail;}
    public void setCountry(String country) {this.country = country;}
    public void setProfileUrl(String profileURL) {this.profileURL = profileURL;}
    public void setNFollowers(Integer nFollowers) {this.nFollowers = nFollowers;}
    public void setImage(String image) {this.image = image;}

}
