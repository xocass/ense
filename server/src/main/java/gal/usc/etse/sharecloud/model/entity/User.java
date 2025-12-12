package gal.usc.etse.sharecloud.model.entity;


import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.*;

import java.time.Instant;
import java.util.Set;

@Document(collection = "users")
public class User {
    @Id
    private String id; //ObjectId generado por MongoDB
    @Indexed(unique = true)
    private String email;

    private String username;
    private String password;
    private Set<String> roles;

    private boolean spotifyLinked = false;
    private String spotifyId;
    private String spotifyAccessToken;
    private String spotifyRefreshToken;
    private Instant spotifyAccessTokenExpiresAt;

    public User(String email, String password, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public String getId() {return id;}
    public String getEmail() {return this.email;}
    public String getUsername() {return this.username;}
    public String getPassword() {return this.password;}
    public Set<String> getRoles() {return this.roles;}
    public String getSpotifyAccessToken() {return this.spotifyAccessToken;}
    public String getSpotifyRefreshToken() {return this.spotifyRefreshToken;}
    public Instant getSpotifyAccessTokenExpiresAt() {return this.spotifyAccessTokenExpiresAt;}
    public String getSpotifyId() {return this.spotifyId;}

    public void setEmail(String email) {this.email = email;}
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setRoles(Set<String> roles) {this.roles = roles;}
    public void setSpotifyAccessToken(String accessToken) {this.spotifyAccessToken = accessToken;}
    public void setSpotifyRefreshToken(String refreshToken) {this.spotifyRefreshToken = refreshToken;}
    public void setSpotifyAccessTokenExpiresAt(Instant expiresAt) {this.spotifyAccessTokenExpiresAt = expiresAt;}
    public void setSpotifyLinked(boolean spotifyLinked) {this.spotifyLinked = spotifyLinked;}
    public void setSpotifyId(String spotifyId) {this.spotifyId = spotifyId;}

    public boolean isSpotifyLinked() {return this.spotifyLinked;}
}
