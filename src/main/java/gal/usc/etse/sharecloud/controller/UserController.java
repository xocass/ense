package gal.usc.etse.sharecloud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gal.usc.etse.sharecloud.service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Servizo para obter o user por email
    @GetMapping("/{email}")
    public User getUserByEmail(@PathVariable String email) {
        // Lóxica para obter o user

    }

    // Servizo para dar de alta un novo usuario
    @PostMapping
    public ResponseEntity<User> signUpUser(@RequestBody User user) {
        // Lóxica para dar de alta un novo usuario


        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
