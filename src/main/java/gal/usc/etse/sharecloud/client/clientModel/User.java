package gal.usc.etse.sharecloud.client.clientModel;



import java.util.List;

public class User {
    private String email;

    private String username;
    private String password;
    private Integer age;
    private String country;
    private String city;

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

    public User(String email, String username, String password, Integer age, String country, String city,
                String spotifyId, String spotifyAccessToken, String spotifyRefreshToken, String spotifyState,
                String spotifyCodeVerifier, String description, List<String> friendsIds, List<Song> favSongs,
                List<Album> favAlbums, List<Artist> favArtists)
    {
        this.email = email;
        this.username = username;
        this.password = password;
        this.age = age;
        this.country = country;
        this.city = city;
        this.spotifyId = spotifyId;
        this.spotifyAccessToken = spotifyAccessToken;
        this.spotifyRefreshToken = spotifyRefreshToken;
        this.spotifyState = spotifyState;
        this.spotifyCodeVerifier = spotifyCodeVerifier;
        this.description = description;
        this.friendsIds = friendsIds;
        this.favSongs = favSongs;
        this.favAlbums = favAlbums;
        this.favArtists = favArtists;
    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }
}
