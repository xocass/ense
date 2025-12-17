package gal.usc.etse.sharecloud.model.entity;

import org.springframework.security.crypto.password.PasswordEncoder;

public class SpotifyProfile {
    private String spotifyID;
    private String displayName;
    private String email;
    private String country;
    private String image;
    private Integer nFollowers;
    private String profileURL;


    public  SpotifyProfile(){}

    public SpotifyProfile(String spotifyID, String display_name, String email, String country, String image,
                          Integer nFollowers, String profileURL) {
        this.spotifyID = spotifyID;
        this.displayName = display_name;
        this.email = email;
        this.country = country;
        this.image = image;
        this.nFollowers = nFollowers;
        this.profileURL = profileURL;
    }

    public static SpotifyProfile from(gal.usc.etse.sharecloud.model.dto.SpotifyProfile spotifyProfile) {
        return new SpotifyProfile(spotifyProfile.spotifyID(), spotifyProfile.displayName(), spotifyProfile.email(),
                spotifyProfile.country(), spotifyProfile.image(), spotifyProfile.nFollowers(), spotifyProfile.profileURL());
    }

    public String getSpotifyID() {return spotifyID;}
    public String getDisplayName() {return displayName;}
    public String getEmail() {return email;}
    public String getCountry() {return country;}
    public String getImage() {return image;}
    public Integer getnFollowers() {return nFollowers;}
    public String getProfileURL() {return profileURL;}

    public void setSpotifyID(String spotifyID) {this.spotifyID = spotifyID;}
    public void setDisplayName(String display_name) {this.displayName = display_name;}
    public void setEmail(String email) {this.email = email;}
    public void setCountry(String country) {this.country = country;}
    public void setImage(String image) {this.image = image;}
    public void setnFollowers(Integer nFollowers) {this.nFollowers = nFollowers;}
    public void setProfileURL(String profileURL) {this.profileURL = profileURL;}
}
