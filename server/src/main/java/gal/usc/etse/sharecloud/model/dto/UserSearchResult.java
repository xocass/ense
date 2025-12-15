package gal.usc.etse.sharecloud.model.dto;

public record UserSearchResult (String id,
                                String username,
                                String image,
                                String country,
                                Boolean isFriend
){
}
