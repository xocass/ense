package gal.usc.etse.sharecloud.model.entity;

import gal.usc.etse.sharecloud.model.dto.UserSearchResult;

import java.util.List;

public record FeedData(List<FeedItem> feedItems,
                       List<UserSearchResult> friends) {
}
