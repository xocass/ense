package gal.usc.etse.sharecloud.server.model.entity;

import com.mongodb.lang.NonNull;
import gal.usc.etse.sharecloud.server.model.Album;
import gal.usc.etse.sharecloud.server.model.Artist;
import gal.usc.etse.sharecloud.server.model.Song;
//import jakarta.persistence.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;
import java.util.stream.Collectors;

//@Entity
@SuppressWarnings("unused")
@Document(collection = "users")
public class User implements UserDetails{
    @Id
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

    /*@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "email"),
            inverseJoinColumns = @JoinColumn(name = "role"))
    private Set<Role> roles;*/
    private Set<Role> roles = new HashSet<>();

    public static User from(gal.usc.etse.sharecloud.server.model.dto.User user, PasswordEncoder passwordEncoder) {
        return new User(user.email(), passwordEncoder.encode(user.password()), null);
    }

    public static User from(gal.usc.etse.sharecloud.server.model.dto.User user) {
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
    public Integer getAge() { return age; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public String getSpotifyId() { return spotifyId; }
    public String getDescription() { return description; }
    public List<String> getFriendsIds() { return friendsIds; }
    public List<Song> getFavSongs() { return favSongs; }
    public List<Album> getFavAlbums() { return favAlbums; }
    public List<Artist> getFavArtists() { return favArtists; }
    public String getSpotifyAccessToken() { return spotifyAccessToken; }
    public String getSpotifyRefreshToken() { return spotifyRefreshToken; }
    public String getSpotifyState() { return spotifyState; }
    public String getSpotifyCodeVerifier() { return spotifyCodeVerifier; }

    // SETTERS

    public void setUsername(String username) { this.username = username;}
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setAge(Integer age) { this.age = age; }
    public void setCountry(String country) { this.country = country; }
    public void setCity(String city) { this.city = city; }
    public void setSpotifyId(String spotifyId) { this.spotifyId = spotifyId; }
    public void setSpotifyAccessToken(String accessToken) { this.spotifyAccessToken = accessToken; }
    public void setSpotifyState(String spotifyState) { this.spotifyState = spotifyState; }
    public void setSpotifyRefreshToken(String refreshToken) { this.spotifyRefreshToken = refreshToken; }
    public void setSpotifyCodeVerifier(String codeVerifier) { this.spotifyCodeVerifier = codeVerifier; }
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
