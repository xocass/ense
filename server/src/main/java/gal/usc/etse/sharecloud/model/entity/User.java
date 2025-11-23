package gal.usc.etse.sharecloud.model.entity;

import com.mongodb.lang.NonNull;
import gal.usc.etse.sharecloud.model.Album;
import gal.usc.etse.sharecloud.model.Artist;
import gal.usc.etse.sharecloud.model.Song;
import gal.usc.etse.sharecloud.model.dto.UserAuth;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Document(collection = "users")
public class User implements UserDetails{
    // Auth
    @Id
    private String email;
    private String password;
    private Set<Role> roles = new HashSet<>();

    // Profile
    private String username;
    private Date birthdate;
    private String country;
    private String city;
    private String image;

    // Atributos para Spotify
    private String spotifyAccessToken;
    private String spotifyRefreshToken;
    private String spotifyState;
    private String spotifyCodeVerifier;
    private Long spotifyAccessTokenExpiresAt;

    private String description;
    private List<String> friendsIds;

    private List<Song> favSongs;
    private List<Album> favAlbums;
    private List<Artist> favArtists;

    /*@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "email"),
            inverseJoinColumns = @JoinColumn(name = "role"))
    private Set<Role> roles;*/
    //private Set<Role> roles = new HashSet<>();

    public static User from(UserAuth user, PasswordEncoder passwordEncoder) {
        return new User(user.email(), passwordEncoder.encode(user.password()), null);
    }

    public static User from(UserAuth user) {
        return new User(user.email(), user.password(), user.roles().stream().map(role -> new Role(role, null, null)).collect(Collectors.toSet()));
    }

    public User(){ this.friendsIds = new ArrayList<>();}

    public User(String email, String password, Set<Role> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.friendsIds = new ArrayList<>();
        this.favSongs = new ArrayList<>();
        this.favAlbums = new ArrayList<>();
        this.favArtists = new ArrayList<>();
    }


    // GETTERS
    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRolename())).toList();
    }
    @NonNull
    public Set<Role> getRoles() {return roles;}
    @Override
    @NonNull
    public String getUsername() { return username;}
    @Override
    @NonNull
    public String getPassword() { return password;}
    public String getEmail() { return email; }
    public Date getBirthdate() { return birthdate; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
    public List<String> getFriendsIds() { return friendsIds; }
    public List<Song> getFavSongs() { return favSongs; }
    public List<Album> getFavAlbums() { return favAlbums; }
    public List<Artist> getFavArtists() { return favArtists; }
    public String getSpotifyAccessToken() { return spotifyAccessToken; }
    public String getSpotifyRefreshToken() { return spotifyRefreshToken; }
    public String getSpotifyState() { return spotifyState; }
    public String getSpotifyCodeVerifier() { return spotifyCodeVerifier; }
    public Long getSpotifyAccessTokenExpiresAt() { return spotifyAccessTokenExpiresAt; }

    // SETTERS

    public void setUsername(String username) { this.username = username;}
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setBirthday(Date birthday) { this.birthdate = birthday; }
    public void setCountry(String country) { this.country = country; }
    public void setCity(String city) { this.city = city; }
    public void setImage(String image) { this.image = image; }
    public void setSpotifyAccessToken(String accessToken) { this.spotifyAccessToken = accessToken; }
    public void setSpotifyState(String spotifyState) { this.spotifyState = spotifyState; }
    public void setSpotifyRefreshToken(String refreshToken) { this.spotifyRefreshToken = refreshToken; }
    public void setSpotifyCodeVerifier(String codeVerifier) { this.spotifyCodeVerifier = codeVerifier; }
    public void setSpotifyAccessTokenExpiresAt(Long expiresAt) { this.spotifyAccessTokenExpiresAt = expiresAt; }
    public void setDescription(String description) { this.description = description; }
    public void setFriendsIds(List<String> friendsIds) { this.friendsIds = friendsIds; }
    public void setFavSongs(List<Song> favSongs) { this.favSongs = favSongs; }
    public void setFavAlbums(List<Album> favAlbums) { this.favAlbums = favAlbums; }
    public void setFavArtists(List<Artist> favArtists) { this.favArtists = favArtists; }
    public User setRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public User addRole(Role role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
        return this;
    }

    public boolean isSpotifyLinked() {
        return spotifyAccessToken != null
                && spotifyRefreshToken != null;
    }

}
