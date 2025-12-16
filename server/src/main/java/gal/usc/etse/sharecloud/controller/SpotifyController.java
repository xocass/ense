package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.SpotifyTokenResponse;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;
import gal.usc.etse.sharecloud.model.entity.User;
import gal.usc.etse.sharecloud.service.SpotifyService;

import gal.usc.etse.sharecloud.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spotify")
@Tag(name = "Spotify", description = "Endpoints para vinculación y acceso a Spotify")
public class SpotifyController {
    private final SpotifyService spotifyService;
    private final UserService userService;

    @Autowired
    public SpotifyController(SpotifyService spotifyService, UserService userService) {
        this.spotifyService = spotifyService;
        this.userService = userService;
    }


    @Operation(
            summary = "Obtener URL de autorización de Spotify",
            description = "Devuelve la URL que el cliente debe abrir para iniciar autorización OAuth con Spotify."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "URL generada correctamente")
    })
    @PreAuthorize("isAnonymous()")
    @GetMapping("/start-link")
    public ResponseEntity<String> getLinkUrl(@RequestParam String email) {
        String url = spotifyService.generateLinkUrl(email);
        return ResponseEntity.ok(url);
    }


    @Operation(
            summary = "Callback de Spotify",
            description = """
                Este endpoint es llamado por Spotify tras la autorización.
                No procesa tokens, solo devuelve una página HTML para que el WebView pueda detectarlo.
                """
    )
    @PreAuthorize("isAnonymous()")
    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state
    ) {
        String html = """
        <html>
        <body>
            <h2>Procesando autenticación de Spotify...</h2>
            <p>Puede cerrar esta ventana.</p>
        </body>
        </html>
        """;

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }


    @Operation(
            summary = "Completar vinculación con Spotify",
            description = """
                El cliente JavaFX envía aquí el code y el email tras interceptar el callback.
                El backend intercambia el code por tokens de Spotify, los guarda, marca spotifyLinked=true,
                obtiene el perfil y actualiza el user en la BD
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vinculación completada"),
            @ApiResponse(responseCode = "400", description = "Error intercambiando tokens")
    })
    @PreAuthorize("isAnonymous()")
    @PostMapping("/complete-link")
    public ResponseEntity<String> completeLink(@RequestParam String code,
                                               @RequestParam String state) throws Exception {
        // Se obtienen los token y se guardan en User
        SpotifyTokenResponse tokens = spotifyService.exchangeCodeForTokens(code);
        User user= spotifyService.updateUserSpotifyTokens(state, tokens);
        userService.saveUser(user);

        // Se obtiene el perfil, se guardan en User y se actualiza User en la BS
        SpotifyProfile spotifyProfile= SpotifyProfile.from(spotifyService.getSpotifyUserProfile(user.getId()));
        user.setSpotifyProfile(spotifyProfile);
        userService.saveUser(user);

        return ResponseEntity.ok("Spotify linked successfully. You may return to the application.");
    }
}
