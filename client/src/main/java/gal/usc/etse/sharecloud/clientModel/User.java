package gal.usc.etse.sharecloud.clientModel;

import gal.usc.etse.sharecloud.clientModel.dto.AuthResponse;
import java.util.Date;
import java.util.List;


public class User {
    private String email;
    private String accessToken;
    private String refreshToken;

    private String username;
    private Date birthDate;
    private String country;
    private String image;

    // Atributos para Spotify
    private String spotifyId;
    private String spotifyAccessToken;
    private String spotifyRefreshToken;
    private String spotifyState;
    private String spotifyCodeVerifier;

    private String description;
    private List<String> friendsIds; // Os IDs son os emails

    private List<Song> favSongs;
    private List<Album> favAlbums;
    private List<Artist> favArtists;

    public User(String email, String username, Date birthDate, String country, String image, String accessToken, String refreshToken){
        this.email = email;
        this.username = username;
        this.birthDate = birthDate;
        this.country = country;
        this.image = image;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static gal.usc.etse.sharecloud.clientModel.User from(AuthResponse user) {
        return new gal.usc.etse.sharecloud.clientModel.User(
                user.user().email(), user.user().username(), user.user().birthdate(), user.user().country(),
                user.user().image(), user.accessToken(), user.refreshToken());
    }

    public String getEmail() {return this.email;}
    public String getUsername() {return this.username;}
    public Date getBirthDate() {return this.birthDate;}
    public String getCountry() {return this.country;}
    public String getImage() {return this.image;}
    public String getAccessToken() {return this.accessToken;}
    public String getRefreshToken() {return this.refreshToken;}
}
