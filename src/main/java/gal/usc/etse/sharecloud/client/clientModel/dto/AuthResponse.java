package gal.usc.etse.sharecloud.client.clientModel.dto;


public record AuthResponse (String accessToken,
                            String refreshToken,
                            UserProfile user){
}
