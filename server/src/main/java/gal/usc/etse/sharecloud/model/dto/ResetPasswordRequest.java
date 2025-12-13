package gal.usc.etse.sharecloud.model.dto;

public record ResetPasswordRequest(String email,
                                   String code,
                                   String newPassword) {
}
