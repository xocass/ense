package gal.usc.etse.sharecloud.server.model.dto;

import com.fasterxml.jackson.annotation.JsonView;
import gal.usc.etse.sharecloud.server.model.entity.Role;
import java.util.Set;
import java.util.stream.Collectors;

public record User (
        @JsonView(Views.Public.class)
        String email,
        @JsonView(Views.Private.class)
        String password,
        @JsonView(Views.Private.class)
        Set<String> roles

) {
    public static User from(gal.usc.etse.sharecloud.server.model.entity.User user) {
        return new User(user.getEmail(), user.getPassword(), user.getRoles().stream().map(Role::getRolename).collect(Collectors.toSet()));
    }


    public interface Views {
        interface Public {}
        interface Private extends Public {}
    }

}
