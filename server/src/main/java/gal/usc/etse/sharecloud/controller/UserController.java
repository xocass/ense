package gal.usc.etse.sharecloud.controller;

import gal.usc.etse.sharecloud.model.dto.UserSearchResult;
import gal.usc.etse.sharecloud.service.UserService;

/*import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
//@Tag(name = "User Social", description = "Búsqueda de usuarios y funcionalidades sociales")
public class UserController {
    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /*@Operation(
            summary = "Buscar usuarios por nombre",
            description = """
                Permite buscar usuarios por username.
                Devuelve un máximo de 10 resultados con información pública básica.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })*/
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search/user")
    public ResponseEntity<List<UserSearchResult>> searchUsers(@RequestParam String userId,
                                                              @RequestParam("query") String query) {
        List<UserSearchResult> results = userService.searchUsers(query, userId);

        return ResponseEntity.ok(results);
    }

}
