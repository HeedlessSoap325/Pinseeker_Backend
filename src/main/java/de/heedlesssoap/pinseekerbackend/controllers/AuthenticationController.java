package de.heedlesssoap.pinseekerbackend.controllers;

import de.heedlesssoap.pinseekerbackend.entities.DTOs.LoginResponseDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.RegistrationDTO;
import de.heedlesssoap.pinseekerbackend.exceptions.UsernameAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.services.AuthenticationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PutMapping("/register")
    public String registerUser(@RequestBody RegistrationDTO body) throws UsernameAlreadyExistsException {
        return authenticationService.registerUser(body.getUsername(), body.getPassword());
    }

    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody RegistrationDTO body){
        return authenticationService.loginUser(body.getUsername(), body.getPassword());
    }
}