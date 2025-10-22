package gal.usc.etse.sharecloud.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "user")
public class User {

    // O email funcionan como PK
    @Id
    private String email;

    private String username;
    private String password;
    private Integer age;
    private String country;
    private String city;

    private String spotifyId;
    private String description;
    private List<String> friendsIds; // Os IDs son os emails

    public User(){
        this.friendsIds = new ArrayList<>();
    }

    public User(String name, String email, String password, Integer age, String country, String city) {
        this.username = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.country = country;
        this.city = city;
        this.friendsIds = new ArrayList<>();
    }

    // GETTERS
    public String getUsername() { return username;}
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Integer getAge() { return age; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public String getSpotifyId() { return spotifyId; }
    public String getDescription() { return description; }
    public List<String> getFriendsIds() { return friendsIds; }

    // SETTERS
    public void setUsername(String username) { this.username = username;}
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setAge(Integer age) { this.age = age; }
    public void setCountry(String country) { this.country = country; }
    public void setCity(String city) { this.city = city; }
    public void setSpotifyId(String spotifyId) { this.spotifyId = spotifyId; }
    public void setDescription(String description) { this.description = description; }
    public void setFriendsIds(List<String> friendsIds) { this.friendsIds = friendsIds; }

}
