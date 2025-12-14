package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.SpotifyProfile;
import gal.usc.etse.sharecloud.model.entity.ActivityType;
import gal.usc.etse.sharecloud.model.entity.UserActivity;
import gal.usc.etse.sharecloud.service.SpotifyActivityService;
import gal.usc.etse.sharecloud.service.SpotifyService;

/*import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user/me/spotify")
//@Tag(name = "Spotify User Data", description = "Endpoints Spotify asociados al usuario autenticado")
public class UserSpotifyController {
    private final SpotifyService spotifyService;
    private final SpotifyActivityService spotifyActivityService;



    @Autowired
    public UserSpotifyController(SpotifyService spotifyService, SpotifyActivityService spotifyActivityService) {
        this.spotifyService = spotifyService;
        this.spotifyActivityService = spotifyActivityService;
    }


    /*@Operation(
            summary = "Obtener perfil de Spotify del usuario",
            description = """
                Devuelve el perfil de Spotify del usuario autenticado.
                Incluye información básica como nombre, email, país e imagen.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil de Spotify obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error al consultar Spotify")
    })*/
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<SpotifyProfile> getSpotifyProfile(@RequestParam String userId) throws Exception {
        SpotifyProfile profile = spotifyService.getSpotifyUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /*@Operation(
            summary = "Obtener últimas canciones escuchadas",
            description = """
                Actualiza y devuelve el estado LISTENED_TRACK del usuario,
                consultando Spotify (/me/player/recently-played).
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canciones obtenidas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error consultando Spotify")
    })*/
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/recently-played")
    public ResponseEntity<UserActivity> getRecentlyPlayedTracks(@RequestParam String userId,
                                                                @RequestParam(defaultValue = "10") int limit)
            throws Exception {
        spotifyActivityService.updateRecentlyPlayedTracks(userId, limit);
        UserActivity activity= spotifyActivityService.getUserActivity(userId, ActivityType.LISTENED_TRACKS);

        return ResponseEntity.ok(activity);
    }

    /*@Operation(
            summary = "Obtener top canciones del usuario",
            description = """
                Actualiza y devuelve el estado MOST_LISTENED_TRACKS,
                usando Spotify (/me/top/tracks).
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top canciones obtenido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error consultando Spotify")
    })*/
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/top-tracks")
    public ResponseEntity<UserActivity> getTopTracks(@RequestParam String userId,
                                                           @RequestParam(defaultValue = "10") int limit)
            throws Exception{
        spotifyActivityService.updateTopTracks(userId, limit);

        UserActivity activity= spotifyActivityService.getUserActivity(userId, ActivityType.TOP_TRACKS);

        return ResponseEntity.ok(activity);
    }

    /*@Operation(
            summary = "Obtener top artistas del usuario",
            description = """
                Actualiza y devuelve el estado MOST_LISTENED_ARTISTS,
                usando Spotify (/me/top/artists).
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top artistas obtenido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error consultando Spotify")
    })*/
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/top-artists")
    public ResponseEntity<UserActivity> getTopArtists(@RequestParam String userId,
                                                            @RequestParam(defaultValue = "10") int limit)
            throws Exception {
        spotifyActivityService.updateTopArtists(userId, limit);

        UserActivity activity= spotifyActivityService.getUserActivity(userId, ActivityType.TOP_ARTISTS);

        return ResponseEntity.ok(activity);
    }
}
