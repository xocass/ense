package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.UserSearchResult;
import gal.usc.etse.sharecloud.model.dto.FriendRequest;
import gal.usc.etse.sharecloud.service.FriendService;
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
@RequestMapping("/api/friend")
@Tag(name = "Friends", description = "Gestión de amistades: solicitudes, aceptación, rechazo y listado de amigos")
public class FriendController {
    private final FriendService friendService;

    @Autowired
    public FriendController(FriendService friendService) {this.friendService = friendService;}



    @Operation(operationId = "sendFriendRequest", summary = "Enviar solicitud de amistad",
            description = """
                    Envía una solicitud de amistad desde un usuario a otro.
                    La solicitud se crea con estado PENDING hasta que el receptor la acepte o la rechace.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud enviada correctamente",
                    links = {@Link(name = "getPendingRequests", operationId = "getPendingFriendRequests",
                                    description = "Consultar solicitudes pendientes del receptor")}),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (ya enviada o a ti mismo)"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping("/request/send")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> sendRequest(@RequestParam String senderId, @RequestParam String receiverId) {
        friendService.sendFriendRequest(senderId, receiverId);
        return ResponseEntity.ok().build();
    }


    @Operation(operationId = "getPendingFriendRequests", summary = "Obtener solicitudes de amistad pendientes",
            description = """
                    Devuelve la lista de solicitudes de amistad recibidas por el usuario que todavía están en estado PENDING.
                    Se usa principalmente para notificaciones.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitudes obtenidas correctamente",
                    links = {@Link(name = "acceptRequest", operationId = "acceptFriendRequest",
                                    description = "Aceptar una solicitud pendiente"),
                            @Link(name = "rejectRequest", operationId = "rejectFriendRequest",
                                    description = "Rechazar una solicitud pendiente")}),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/request/list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FriendRequest>> getRequests(@RequestParam String id) {
        return ResponseEntity.ok(friendService.getPendingRequests(id));
    }


    @Operation(operationId = "acceptFriendRequest", summary = "Aceptar solicitud de amistad",
            description = """
                    Acepta una solicitud de amistad pendiente.
                    La solicitud pasa a estado ACCEPTED y ambos usuarios pasan a ser amigos.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud aceptada correctamente",
                    links = {@Link(name = "getFriends", operationId = "getFriends",
                                    description = "Obtener la lista de amigos del usuario")}),
            @ApiResponse(responseCode = "403", description = "No autorizado para aceptar esta solicitud"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PatchMapping("/request/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> acceptRequest(@RequestParam String requestId, @RequestParam String id) {
        friendService.acceptRequest(requestId, id);
        return ResponseEntity.ok().build();
    }


    @Operation(operationId = "rejectFriendRequest", summary = "Rechazar solicitud de amistad",
            description = """
                    Rechaza una solicitud de amistad pendiente. La solicitud pasa a estado REJECTED y no se crea
                    ninguna relación de amistad.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud rechazada correctamente",
                    links = {@Link(name = "getPendingRequests", operationId = "getPendingFriendRequests",
                                    description = "Consultar solicitudes pendientes restantes")}),
            @ApiResponse(responseCode = "403", description = "No autorizado para rechazar esta solicitud"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PatchMapping("/request/reject")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> rejectRequest(@RequestParam String requestId, @RequestParam String id) {
        friendService.rejectRequest(requestId, id);
        return ResponseEntity.ok().build();
    }


    @Operation(operationId = "getFriends", summary = "Obtener amigos del usuario",
            description = """
                    Devuelve la lista de amigos del usuario autenticado. Cada amigo incluye información básica de perfil.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de amigos obtenida correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public ResponseEntity<List<UserSearchResult>> getFriends(@RequestParam String id) {
        return ResponseEntity.ok(friendService.getFriends(id));
    }
}
