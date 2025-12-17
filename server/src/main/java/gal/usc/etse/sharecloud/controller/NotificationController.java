package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.LikeRequest;
import gal.usc.etse.sharecloud.model.entity.Reaccion;
import gal.usc.etse.sharecloud.model.entity.ReactionType;
import gal.usc.etse.sharecloud.model.entity.User;
import gal.usc.etse.sharecloud.repository.ReactionRepository;
import gal.usc.etse.sharecloud.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

import static gal.usc.etse.sharecloud.service.FeedService.nextMidnight;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/user/me/notifications")
public class NotificationController {

    private final UserRepository userRepo;
    private final ReactionRepository reaccionRepo;

    public NotificationController(UserRepository userRepo, ReactionRepository reaccionRepo) {
        this.userRepo = userRepo;
        this.reaccionRepo = reaccionRepo;
    }

    @GetMapping
    public ResponseEntity<List<Reaccion>> myNotifications(Authentication auth) {
        String email = auth.getName();
        User me = userRepo.findByEmail(email).orElseThrow();

        List<Reaccion> list = reaccionRepo.findByReceiverIdOrderByCreatedAtDesc(me.getId());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/like")
    public ResponseEntity<Void> likeTrack(
            @RequestBody LikeRequest dto,
            Authentication authentication
    ) {
        // sender desde JWT
        String email = authentication.getName();
        User sender = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // receiver
        User receiver = userRepo.findById(dto.receiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Reaccion reaccion = new Reaccion();
        reaccion.setSenderId(sender.getId());
        reaccion.setReceiverId(receiver.getId());
        reaccion.setReactionType(ReactionType.LIKE);
        reaccion.setTrackName(dto.trackName());
        reaccion.setComentario(null);
        reaccion.setCreatedAt(Instant.now());
        reaccion.setExpiresAt(nextMidnight());

        reaccionRepo.save(reaccion);

        return ResponseEntity.status(201).build();
    }
}
