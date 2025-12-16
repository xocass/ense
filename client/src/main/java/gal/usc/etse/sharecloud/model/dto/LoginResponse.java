package gal.usc.etse.sharecloud.model.dto;

public record LoginResponse(String accessToken,
                            String userID,
                            String username,
                            String image
) {
}
