package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.SpotifyProfile;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopArtistsResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopTracksResponse;
import gal.usc.etse.sharecloud.model.entity.ActivityType;
import gal.usc.etse.sharecloud.model.entity.UserActivity;
import gal.usc.etse.sharecloud.service.SpotifyActivityService;
import gal.usc.etse.sharecloud.service.SpotifyService;

/*import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;*/
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user/me/spotify")
@Tag(name = "Spotify User Data", description = "Endpoints Spotify asociados al usuario autenticado")
public class UserSpotifyController {
    private final SpotifyService spotifyService;
    private final SpotifyActivityService spotifyActivityService;



    @Autowired
    public UserSpotifyController(SpotifyService spotifyService, SpotifyActivityService spotifyActivityService) {
        this.spotifyService = spotifyService;
        this.spotifyActivityService = spotifyActivityService;
    }


    @Operation(
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
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<SpotifyProfile> getSpotifyProfile(@RequestParam String id) throws Exception {
        SpotifyProfile profile = spotifyService.getSpotifyUserProfile(id);
        return ResponseEntity.ok(profile);
    }

    @Operation(
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
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/recently-played")
    public ResponseEntity<SpotifyRecentlyPlayedResponse> getRecentlyPlayedTracks(@RequestParam String id,
                                                                      @RequestParam(defaultValue = "10") int limitSave,
                                                                                 @RequestParam(defaultValue = "10") int limitReturn)
            throws Exception {
        SpotifyRecentlyPlayedResponse response = spotifyActivityService.returnListenedTrackState(id, limitSave, limitReturn);


        return ResponseEntity.ok(response);
    }

    @Operation(
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
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/top-tracks")
    public ResponseEntity<SpotifyTopTracksResponse> getTopTracks(@RequestParam String id,
                                                                 @RequestParam(defaultValue = "10") int limit)
            throws Exception{
        return ResponseEntity.ok(spotifyActivityService.returnTopTracks(id, limit));
    }

    @Operation(
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
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/top-artists")
    public ResponseEntity<SpotifyTopArtistsResponse> getTopArtists(@RequestParam String id,
                                                                   @RequestParam(defaultValue = "10") int limit)
            throws Exception {
        return ResponseEntity.ok(spotifyActivityService.returnTopArtists(id, limit));
    }
}
