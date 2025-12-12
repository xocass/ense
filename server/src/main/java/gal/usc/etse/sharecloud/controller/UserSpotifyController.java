package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.SpotifyProfile;
import gal.usc.etse.sharecloud.service.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/{id}/spotify")
public class UserSpotifyController {
    private final SpotifyService spotifyService;



    @Autowired
    public UserSpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @PreAuthorize("#id == authentication.principal.username")
    @GetMapping("/me")
    public ResponseEntity<SpotifyProfile> getSpotifyProfile(@PathVariable String id) throws Exception {
        SpotifyProfile profile = spotifyService.getSpotifyUserProfile(id);
        return ResponseEntity.ok(profile);
    }
}
