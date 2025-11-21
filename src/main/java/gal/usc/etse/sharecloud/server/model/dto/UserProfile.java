package gal.usc.etse.sharecloud.server.model.dto;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.Date;

public record UserProfile (
        @JsonView(Views.Public.class)
        String email,
        @JsonView(Views.Public.class)
        String username,
        @JsonView(Views.Public.class)
        Date birthdate,
        @JsonView(Views.Public.class)
        String country,
        @JsonView(Views.Public.class)
        String city,
        @JsonView(Views.Public.class)
        String image
){
    public static UserProfile from(gal.usc.etse.sharecloud.server.model.entity.User user) {
        return new UserProfile(user.getEmail(), user.getUsername(), user.getBirthdate(), user.getCountry(), user.getCity(), user.getImage());
    }


    public interface Views {
        interface Public {}
        interface Private extends Public {}
    }
}
