package gal.usc.etse.sharecloud.controller;
import gal.usc.etse.sharecloud.exception.UserAlreadyExistsException;
import gal.usc.etse.sharecloud.service.UserService;
import gal.usc.etse.sharecloud.model.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
 class UserController {
    UserService userService;
    ObjectMapper mapper;

    @Autowired
    public UserController(UserService userService, ObjectMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    // Servizo para obter o user por email. Si atopa users con ese email, devolve un obxecto user.
    // Se non, devolve null
    @GetMapping("/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUser(email);
    }

    // Servizo para rexistrar un novo usuario. Aquí xa se fixeron as comprobacións previas necesarias.
    // No obxecto user ven todos os datos que insertou o usuario á hora de rexistrarse.
    @PostMapping
    public ResponseEntity<User> signUpUser(@RequestBody User temp) throws UserAlreadyExistsException {
        User user= userService.signUpUser(temp);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // Servizo para obter a lista de amigos do user aportado
    /*@GetMapping("/{email}")
    public List<User> getFriends(@PathVariable String email) {
        return userService.getFriends(email);
    }*/
}
