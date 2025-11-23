package gal.usc.etse.sharecloud.model.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserProfile user
) {}