package de.heedlesssoap.pinseekerbackend.controllers;

import de.heedlesssoap.pinseekerbackend.entities.DTOs.LoginResponseDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.RegistrationDTO;
import de.heedlesssoap.pinseekerbackend.exceptions.UsernameAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.services.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PutMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody RegistrationDTO body) throws UsernameAlreadyExistsException {
        return authenticationService.registerUser(body.getUsername(), body.getPassword());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody RegistrationDTO body){
        return authenticationService.loginUser(body.getUsername(), body.getPassword());
    }
}