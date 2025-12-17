package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.FeedItem;
import gal.usc.etse.sharecloud.service.FeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/me/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/listened-tracks")
    public ResponseEntity<List<FeedItem>> getFriendsListenedTracks(@RequestParam String id
    ) {

        List<FeedItem> feed = feedService.getFriendsFeed(id);

        return ResponseEntity.ok(feed);
    }


}
