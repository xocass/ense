package gal.usc.etse.sharecloud.server.controller;

import com.fasterxml.jackson.annotation.JsonView;
import gal.usc.etse.sharecloud.server.service.UserService;
import gal.usc.etse.sharecloud.server.model.dto.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
 public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Obtener todos los usuarios
    @GetMapping(path="all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    @JsonView(UserAuth.Views.Public.class)
    public ResponseEntity<List<UserAuth>> get() {
        return ResponseEntity.ok(userService.get());
    }

    // Paginación
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    @JsonView(UserAuth.Views.Public.class)
    public ResponseEntity<Page<UserAuth>> get(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int pagesize,
            @RequestParam(value = "sort", required = false, defaultValue = "") List<String> sort
    ) {
        return ResponseEntity.ok(
                userService.get(
                        PageRequest.of(
                                page,
                                pagesize,
                                Sort.by(sort.stream().map(key -> key.startsWith("-") ? Sort.Order.desc(key.substring(1)) : Sort.Order.asc(key)).toList())
                        )
                )
        );
    }

    // Obtener usuario por email
    @GetMapping(path = "{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    @JsonView(UserAuth.Views.Public.class)
    public ResponseEntity<UserAuth> get(@PathVariable String email){
        return ResponseEntity.ok(userService.get(email));
    }

    // >>>>>>>>>>>>>   SPOTIFY
    // Spotify OAuth: iniciar enlace
    @GetMapping("/spotify/link")
    public ResponseEntity<String> startSpotifyLink(@RequestParam String email) {
        try {
            String url = userService.startSpotifyLink(email);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    // Spotify OAuth: completar enlace con callback
    @GetMapping("/spotify/callback")
    public ResponseEntity<Void> spotifyCallback(@RequestParam String code, @RequestParam String state) {
        try {
            userService.completeSpotifyLink(code, state);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // si quieres mostrar página bonita en navegador, devolver redirección a página estática
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Obtener ultima cancion escuchada
    @GetMapping("/{email}/spotify/last-track")
    public ResponseEntity<Map<String, Object>> getLastTrack(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userService.getLastPlayedTrack(email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
    // Obtener perfil spotify
    @GetMapping("/{email}/spotify/profile")
    public ResponseEntity<Map<String, Object>> getSpotifyProfile(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userService.getSpotifyProfile(email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }



    // Servizo para rexistrar un novo usuario. Aquí xa se fixeron as comprobacións previas necesarias.
    // No obxecto user ven todos os datos que insertou o usuario á hora de rexistrarse.
    /*@PostMapping
    public ResponseEntity<User> signUpUser(@RequestBody User temp) throws DuplicateUserException {
        User user= userService.create(temp);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }*/
}
