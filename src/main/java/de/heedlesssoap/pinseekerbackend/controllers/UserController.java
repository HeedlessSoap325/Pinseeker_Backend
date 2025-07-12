package de.heedlesssoap.pinseekerbackend.controllers;

import de.heedlesssoap.pinseekerbackend.entities.DTOs.ExtendedApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.UpdateApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class UserController {
    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody UpdateApplicationUserDTO updateApplicationUserDTO){
        return userService.createUser(updateApplicationUserDTO);
    }

    @PutMapping("/")
    public ResponseEntity<String> updateUser(@RequestHeader("Authorization") String token, @RequestHeader("password") String password, @RequestBody UpdateApplicationUserDTO updateApplicationUserDTO){
        return userService.updateUser(token, password, updateApplicationUserDTO);
    }

    @PutMapping("/picture")
    public ResponseEntity<String> updateUserProfilePicture(@RequestHeader("Authorization") String token, @RequestParam(value = "image") MultipartFile image){
        return userService.updatePicture(token, image);
    }

    @DeleteMapping("/picture")
    public ResponseEntity<String> deleteUserProfilePicture(@RequestHeader("Authorization") String token){
        return userService.deletePicture(token);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<ExtendedApplicationUserDTO> getUser(@PathVariable("user_id") Integer user_id){
        return userService.getUser(user_id);
    }

    @DeleteMapping("/")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token, @RequestBody String password){
        return userService.deleteUser(token, password);
    }
}