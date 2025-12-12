package gal.usc.etse.sharecloud.model.dto;

import java.util.Set;

public record AuthenticatedUser(
        String userId,
        Set<String> roles
) {}
