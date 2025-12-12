package gal.usc.etse.sharecloud.model.dto;

public record SessionTokens(String userId,
                            String accessToken,
                            String refreshToken)
{}
