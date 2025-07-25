package de.heedlesssoap.pinseekerbackend.controllers;

import de.heedlesssoap.pinseekerbackend.entities.DTOs.BasicApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.ExtendedApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.PinDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.UpdateApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.exceptions.UsernameAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

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
    public ResponseEntity<Map<String, String>> updateUser(@RequestHeader("Authorization") String token, @RequestHeader("password") String password, @RequestBody UpdateApplicationUserDTO updateApplicationUserDTO) throws InvalidJWTTokenException, AccessDeniedException, UsernameAlreadyExistsException {
        return userService.updateUser(token, password, updateApplicationUserDTO);
    }

    @PutMapping("/picture")
    public ResponseEntity<Map<String, String>> updateUserProfilePicture(@RequestHeader("Authorization") String token, @RequestParam(value = "image") MultipartFile image) throws InvalidJWTTokenException, IOException {
        return userService.updatePicture(token, image);
    }

    @DeleteMapping("/picture")
    public ResponseEntity<Map<String, String>> deleteUserProfilePicture(@RequestHeader("Authorization") String token) throws InvalidJWTTokenException, IOException{
        return userService.deletePicture(token);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<ExtendedApplicationUserDTO> getUser(@RequestHeader("Authorization") String token, @PathVariable("user_id") Integer user_id) throws InvalidJWTTokenException{
        return userService.getUser(token, user_id);
    }

    @DeleteMapping("/")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestHeader("Authorization") String token, @RequestBody String password) throws InvalidJWTTokenException, IOException {
        return userService.deleteUser(token, password);
    }

    @GetMapping("/{user_id}/found_pins")
    public ResponseEntity<Set<PinDTO>> getUserFoundPins(@RequestHeader("Authorization") String token, @PathVariable("user_id") Integer user_id) throws InvalidJWTTokenException {
        return userService.getFoundPins(token, user_id);
    }

    @GetMapping("/{user_id}/hidden_pins")
    public ResponseEntity<Set<PinDTO>> getUserHiddenPins(@RequestHeader("Authorization") String token, @PathVariable("user_id") Integer user_id) throws InvalidJWTTokenException {
        return userService.getHiddenPin(token, user_id);
    }

    @PostMapping("/upgrade")
    public ResponseEntity<Map<String, String>> upgradeUser(@RequestHeader("Authorization") String token) throws InvalidJWTTokenException {
        return userService.upgradeUser(token);
    }

    @GetMapping("/search")
    public ResponseEntity<Set<BasicApplicationUserDTO>> findUsersByUsername(@RequestHeader("Authorization") String token, @RequestBody String username) throws InvalidJWTTokenException {
        return userService.findUsersByUsername(token, username);
    }
}