package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.SpotifyProfile;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopArtistsResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopTracksResponse;
import gal.usc.etse.sharecloud.service.SpotifyActivityService;
import gal.usc.etse.sharecloud.service.SpotifyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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



    @Operation(operationId = "getMySpotifyProfile", summary = "Obtener perfil de Spotify del usuario autenticado",
            description = """
                    Devuelve el perfil de Spotify del usuario autenticado.
                    Incluye nombre visible, email, país e imagen de perfil.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil de Spotify obtenido correctamente",
                    links = {@Link(name = "recentlyPlayed", operationId = "getMyRecentlyPlayed",
                                    description = "Consultar canciones escuchadas recientemente"),
                            @Link(name = "topTracks", operationId = "getMyTopTracks",
                                    description = "Consultar canciones más escuchadas"),
                            @Link(name = "topArtists", operationId = "getMyTopArtists",
                                    description = "Consultar artistas más escuchados")}
            ),
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


    @Operation(operationId = "getMyRecentlyPlayed", summary = "Obtener últimas canciones escuchadas",
            description = """
                    Consulta Spotify (/me/player/recently-played), actualiza el estado LISTENED_TRACK del usuario
                    y devuelve las canciones más recientes.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canciones obtenidas correctamente",
                    links = {@Link(name = "topTracks", operationId = "getMyTopTracks",
                                    description = "Consultar top canciones"),
                            @Link(name = "topArtists", operationId = "getMyTopArtists",
                                    description = "Consultar top artistas")}
            ),
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


    @Operation(operationId = "getMyTopTracks", summary = "Obtener top canciones del usuario",
            description = """
                    Consulta Spotify (/me/top/tracks), actualiza el estado MOST_LISTENED_TRACKS
                    y devuelve las canciones más escuchadas.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top canciones obtenido",
                    links = {@Link(name = "topArtists", operationId = "getMyTopArtists",
                                    description = "Consultar top artistas"),
                            @Link(name = "recentlyPlayed", operationId = "getMyRecentlyPlayed",
                                    description = "Consultar actividad reciente")}
            ),
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


    @Operation(operationId = "getMyTopArtists", summary = "Obtener top artistas del usuario",
            description = """
                    Consulta Spotify (/me/top/artists), actualiza el estado MOST_LISTENED_ARTISTS
                    y devuelve los artistas más escuchados.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top artistas obtenido",
                    links = {@Link(name = "topTracks", operationId = "getMyTopTracks", description = "Consultar top canciones"),
                            @Link(name = "recentlyPlayed", operationId = "getMyRecentlyPlayed",
                                    description = "Consultar actividad reciente")}
            ),
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/following/{id}")
    public ResponseEntity<String> isFollowing(@RequestParam String currID, @PathVariable String id)
            throws Exception {
        return ResponseEntity.ok(spotifyService.isFollowingUser(id,currID));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/do-follow/{spotyid}")
    public ResponseEntity<Boolean> doFollow(@RequestParam String currID, @PathVariable String spotyid) throws Exception {
        spotifyService.doFollow(spotyid,currID);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/do-unfollow/{spotyid}")
    public ResponseEntity<Boolean> doUnfollow(@RequestParam String currID, @PathVariable String spotyid) throws Exception {
        spotifyService.doUnfollow(spotyid,currID);
        return ResponseEntity.noContent().build();
    }
}
