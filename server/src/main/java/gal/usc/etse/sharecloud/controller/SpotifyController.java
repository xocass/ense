package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.SpotifyTokenResponse;
import gal.usc.etse.sharecloud.model.entity.SpotifyProfile;
import gal.usc.etse.sharecloud.model.entity.User;
import gal.usc.etse.sharecloud.service.SpotifyService;

import gal.usc.etse.sharecloud.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.links.Link;
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
@Tag(name = "Spotify", description = "Vinculación de cuentas y acceso a datos de Spotify mediante OAuth 2.0")
public class SpotifyController {
    private final SpotifyService spotifyService;
    private final UserService userService;

    @Autowired
    public SpotifyController(SpotifyService spotifyService, UserService userService) {
        this.spotifyService = spotifyService;
        this.userService = userService;
    }


    @Operation(operationId = "startSpotifyLink", summary = "Obtener URL de autorización de Spotify",
            description = """
                    Genera la URL de autorización OAuth de Spotify.
                    El cliente debe abrir esta URL (WebView) para que el usuario conceda permisos a la aplicación.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "URL de autorización generada correctamente",
                    links = {@Link(name = "callback", operationId = "spotifyCallback",
                                    description = "Endpoint al que Spotify redirige tras la autorización"
                    ),
                            @Link(name = "completeLink", operationId = "completeSpotifyLink",
                                    description = "Completar la vinculación tras recibir el código OAuth")}
            ),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @PreAuthorize("isAnonymous()")
    @GetMapping("/start-link")
    public ResponseEntity<String> getLinkUrl(@RequestParam String email) {
        String url = spotifyService.generateLinkUrl(email);
        return ResponseEntity.ok(url);
    }


    @Operation(operationId = "spotifyCallback", summary = "Callback OAuth de Spotify",
            description = """
                    Endpoint llamado directamente por Spotify tras la autorización del usuario.
                    Devuelve una página HTML mínima para que el cliente
                    (WebView) pueda detectar la redirección.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Callback recibido correctamente",
                    links = {@Link(name = "completeLink", operationId = "completeSpotifyLink",
                                    description = "Completar la vinculación con el código recibido")})
    })
    @PreAuthorize("isAnonymous()")
    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state
    ) {
        String html = """
                    <!DOCTYPE html>
                    <html lang="es">
                    <head>
                        <meta charset="UTF-8">
                        <title>Spotify – ShareCloud</title>
                        <style>
                            body {
                                margin: 0;
                                height: 100vh;
                                font-family: Arial, sans-serif;
                                background-color: #121212;
                                color: white;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                            }
                    
                            .container {
                                text-align: center;
                            }
                    
                            h2 {
                                color: #1DB954;
                                margin-bottom: 10px;
                            }
                    
                            p {
                                font-size: 14px;
                                opacity: 0.9;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h2>Conectando con Spotify</h2>
                            <p>La vinculación se ha completado correctamente.</p>
                            <p>Ya puedes volver a la aplicación.</p>
                        </div>
                    </body>
                    </html>
                    """;
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }


    @Operation(operationId = "completeSpotifyLink", summary = "Completar vinculación con Spotify",
            description = """
                    Completa el proceso de vinculación con Spotify.
                    
                    El backend:
                    - Intercambia el código OAuth por tokens
                    - Guarda los tokens en el usuario
                    - Marca spotifyLinked = true
                    - Obtiene el perfil del usuario en Spotify
                    - Actualiza el usuario en la BD
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vinculación con Spotify completada correctamente",
                    links = {@Link(name = "getSpotifyProfile", operationId = "getSpotifyProfile",
                                    description = "Consultar el perfil de Spotify del usuario")}
            ),
            @ApiResponse(responseCode = "400", description = "Error intercambiando el código OAuth"),
            @ApiResponse(responseCode = "500", description = "Error interno al comunicarse con Spotify")
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
