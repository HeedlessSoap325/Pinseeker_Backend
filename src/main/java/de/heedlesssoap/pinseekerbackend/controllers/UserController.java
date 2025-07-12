package de.heedlesssoap.pinseekerbackend.controllers;

import de.heedlesssoap.pinseekerbackend.services.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class UserController {
    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
    @PostMapping("/")
    public ResponseEntity<String> createUser(@RequestHeader("Authorization") String token){
        return userService.createUser(...);
    }

    @PutMapping("/")
    public ResponseEntity<String> updateUser(@RequestHeader("Authorization") String token){
        return userService.updateUser(...);
    }

    @PutMapping("/picture")
    public ResponseEntity<String> updateUserProfilePicture(@RequestHeader("Authorization") String token){
        return userService.updatePicture(...);
    }

    @DeleteMapping("/picture")
    public ResponseEntity<String> deleteUserProfilePicture(@RequestHeader("Authorization") String token){
        return userService.deletePicture(...);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<DTO> getUser(@PathVariable("user_id") Integer user_id){
        return userService.getUser(user_id);
    }

    @DeleteMapping("/")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token){
        return userService.deleteUser(...);
    }
    **/
}