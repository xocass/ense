package gal.usc.etse.sharecloud.server.model.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserProfile user
) {}