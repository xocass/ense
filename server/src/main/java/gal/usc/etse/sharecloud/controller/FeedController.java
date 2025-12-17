package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.FeedItem;
import gal.usc.etse.sharecloud.model.entity.Like;
import gal.usc.etse.sharecloud.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import gal.usc.etse.sharecloud.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static gal.usc.etse.sharecloud.service.FeedService.nextMidnight;

@RestController
@RequestMapping("/api/user/me/feed")
public class FeedController {

    private final FeedService feedService;
    private final FriendService  friendService;

    @Autowired
    public FeedController(FeedService feedService,  FriendService friendService) {
        this.feedService = feedService;
        this.friendService = friendService;
    }



    @Operation(
            operationId = "getFriendsListenedTracks",
            summary = "Obtener feed musical de amigos",
            description = "Devuelve las canciones escuchadas por los amigos del usuario en las últimas 24 horas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feed obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/listened-tracks")
    public ResponseEntity<List<FeedItem>> getFriendsListenedTracks(@RequestParam String id) {
        List<FeedItem> feed = feedService.getFriendsFeed(id);

        return ResponseEntity.ok(feed);
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like/give")
    public ResponseEntity<Void> likeTrack(@RequestParam String id, @RequestParam String friendId, @RequestParam String trackName) {
        friendService.giveLike(id, friendId, trackName);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/like/receive")
    public ResponseEntity<List<gal.usc.etse.sharecloud.model.dto.Like>> receiveLikes(@RequestParam String id) {
        List<Like> likes= friendService.receiveLikes(id);
        List<gal.usc.etse.sharecloud.model.dto.Like> likesDTO= new ArrayList<>();
        for (Like like : likes) {
            likesDTO.add(gal.usc.etse.sharecloud.model.dto.Like.from(like));
        }
        return ResponseEntity.ok(likesDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/like")
    public ResponseEntity<Void> deleteLike(@RequestParam String likeId) {
        friendService.deleteLike(likeId);
        return ResponseEntity.ok().build();
    }



}
