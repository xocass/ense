package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.SpotifyProfile;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.service.SpotifyActivityService;
import gal.usc.etse.sharecloud.service.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/me/spotify")
public class UserSpotifyController {
    private final SpotifyService spotifyService;
    private final SpotifyActivityService activityService;



    @Autowired
    public UserSpotifyController(SpotifyService spotifyService, SpotifyActivityService activityService) {
        this.spotifyService = spotifyService;
        this.activityService = activityService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<SpotifyProfile> getSpotifyProfile(@RequestParam String id) throws Exception {
        SpotifyProfile profile = spotifyService.getSpotifyUserProfile(id);
        return ResponseEntity.ok(profile);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/recently-played")
    public ResponseEntity<SpotifyRecentlyPlayedResponse> getRecentlyPlayed(@RequestParam String id, @RequestParam int limit) throws Exception {
        SpotifyRecentlyPlayedResponse recentlyPlayed;
        if(limit==10) recentlyPlayed= activityService.returnListenedTrackState(id,limit);
        else recentlyPlayed= spotifyService.getRecentlyPlayed(id,limit);
        return ResponseEntity.ok(recentlyPlayed);
    }
}
