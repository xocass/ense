package gal.usc.etse.sharecloud.model.dto;

public record UserBooleans(String userId,
                           Boolean isFriend,
                           Boolean isFollowing,
                           Boolean isPending
) {
}
