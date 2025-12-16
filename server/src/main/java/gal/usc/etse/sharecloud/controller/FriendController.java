package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.UserSearchResult;
import gal.usc.etse.sharecloud.model.entity.FriendRequest;
import gal.usc.etse.sharecloud.service.FriendService;
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
@RequestMapping("/api/friend")
@Tag(name = "Friends", description = "Gestión de amistades")
public class FriendController {
    private final FriendService friendService;

    @Autowired
    public FriendController(FriendService friendService) {this.friendService = friendService;}


    @Operation(
            summary = "Enviar solicitud de amistad",
            description = "Envía una solicitud de amistad desde un usuario a otro. " +
                          "La solicitud quedará en estado PENDING hasta ser aceptada o rechazada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud enviada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (a ti mismo o ya enviada)"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/request/send")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> sendRequest(@RequestParam String senderId, @RequestParam String receiverId) {
        friendService.sendFriendRequest(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Obtener solicitudes de amistad pendientes",
            description = "Devuelve la lista de solicitudes de amistad recibidas por el usuario " +
                          "que todavía están en estado PENDING. Se usa para notificaciones."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitudes obtenidas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/request/list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FriendRequest>> getRequests(@RequestParam String id) {
        return ResponseEntity.ok(friendService.getPendingRequests(id));
    }

    /*@Operation(
            summary = "Aceptar solicitud de amistad",
            description = "Acepta una solicitud de amistad pendiente. " +
                          "Ambos usuarios pasarán a ser amigos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud aceptada"),
            @ApiResponse(responseCode = "403", description = "No autorizado para aceptar esta solicitud"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })*/
    @PostMapping("/request/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> acceptRequest(@RequestParam String requestId, @RequestParam String id) {
        friendService.acceptRequest(requestId, id);
        return ResponseEntity.ok().build();
    }

    /*@Operation(
            summary = "Rechazar solicitud de amistad",
            description = "Rechaza una solicitud de amistad pendiente. " +
                          "La solicitud pasará a estado REJECTED."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud rechazada"),
            @ApiResponse(responseCode = "403", description = "No autorizado para rechazar esta solicitud"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })*/
    @PostMapping("/request/reject")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> rejectRequest(@RequestParam String requestId, @RequestParam String id) {
        friendService.rejectRequest(requestId, id);
        return ResponseEntity.ok().build();
    }

    /*@Operation(
            summary = "Obtener amigos del usuario",
            description = "Devuelve la lista de amigos del usuario autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de amigos devuelta correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })*/
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public ResponseEntity<List<UserSearchResult>> getFriends(@RequestParam String id) {
        return ResponseEntity.ok(friendService.getFriends(id));
    }
}
