package gal.usc.etse.sharecloud.model.dto;

import com.fasterxml.jackson.annotation.JsonView;
import gal.usc.etse.sharecloud.model.entity.User;

import java.util.Set;

public record UserAuth(
        @JsonView(Views.Public.class)
        String email,

        @JsonView(Views.Private.class)
        String password,

        @JsonView(Views.Private.class)
        Set<String> roles
) {
    public static UserAuth from(User user) {
        return new UserAuth(
                user.getEmail(),
                user.getPassword(),
                user.getRoles()
        );
    }

    public interface Views {
        interface Public {}
        interface Private extends Public {}
    }
}

