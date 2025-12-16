package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.*;
import gal.usc.etse.sharecloud.model.entity.UserActivity;
import gal.usc.etse.sharecloud.service.SpotifyActivityService;
import gal.usc.etse.sharecloud.service.UserActivityService;
import gal.usc.etse.sharecloud.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Social", description = "Búsqueda de usuarios y funcionalidades sociales (perfil Spotify y actividad musical)")
public class UserController {
    private final UserService userService;
    private final UserActivityService activityService;


    @Autowired
    public UserController(UserService userService, UserActivityService activityService) {
        this.userService = userService;
        this.activityService = activityService;
    }


    @Operation(
            summary = "Buscar usuarios por nombre",
            description = """
                Permite buscar usuarios por username.
                Devuelve un máximo de 10 resultados con información pública básica.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search/user")
    public ResponseEntity<List<UserSearchResult>> searchUsers(@RequestParam String id,
                                                              @RequestParam("query") String query) {
        List<UserSearchResult> results = userService.searchUsers(query, id);

        return ResponseEntity.ok(results);
    }

    @Operation(
            summary = "Obtener perfil de Spotify de un usuario",
            description = "Devuelve el perfil público de Spotify (display name, imagen, país, etc.) "
                    + "de un usuario dado su ID. Requiere autenticación."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil de Spotify obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al obtener el perfil de Spotify")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/spotify/profile")
    public ResponseEntity<SpotifyProfile> returnSpotifyProfile(@PathVariable String id) {
        System.out.println("ENTRE");
        SpotifyProfile result = userService.returnSpotifyProfile(id);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Obtener canciones escuchadas recientemente por un usuario",
            description = "Devuelve la lista de canciones escuchadas recientemente por un usuario "
                    + "a través de su cuenta de Spotify vinculada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canciones recientes obtenidas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado o Spotify no vinculado"),
            @ApiResponse(responseCode = "500", description = "Error al consultar Spotify")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/spotify/recently-played")
    public ResponseEntity<SpotifyRecentlyPlayedResponse> returnRecentlyPlayed(@PathVariable String id) throws Exception {
        SpotifyRecentlyPlayedResponse result = activityService.returnOtherRecentlyPlayed(id);
        return ResponseEntity.ok(result);
    }


    @Operation(
            summary = "Obtener top canciones de un usuario",
            description = "Devuelve las canciones más escuchadas (top tracks) del usuario "
                    + "según Spotify. Normalmente top 5 o top 10."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top canciones obtenidas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado o Spotify no vinculado"),
            @ApiResponse(responseCode = "500", description = "Error al consultar Spotify")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/spotify/top-tracks")
    public ResponseEntity<SpotifyTopTracksResponse> returnTopTracks(@PathVariable String id) throws Exception {
        SpotifyTopTracksResponse result = activityService.returnOtherTopTracks(id);
        return ResponseEntity.ok(result);
    }


    @Operation(
            summary = "Obtener top artistas de un usuario",
            description = "Devuelve los artistas más escuchados (top artists) del usuario "
                    + "según Spotify."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top artistas obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado o Spotify no vinculado"),
            @ApiResponse(responseCode = "500", description = "Error al consultar Spotify")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/spotify/top-artists")
    public ResponseEntity<SpotifyTopArtistsResponse> returnTopArtists(@PathVariable String id) throws Exception {
        SpotifyTopArtistsResponse result = activityService.returnOtherTopArtists(id);
        return ResponseEntity.ok(result);
    }

}
