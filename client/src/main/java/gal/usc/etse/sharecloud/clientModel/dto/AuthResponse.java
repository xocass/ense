package gal.usc.etse.sharecloud.clientModel.dto;


public record AuthResponse (String accessToken,
                            String refreshToken,
                            UserProfile user){
}
