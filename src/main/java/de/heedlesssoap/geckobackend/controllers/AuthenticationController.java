package de.heedlesssoap.geckobackend.controllers;

import de.heedlesssoap.geckobackend.entities.DTOs.LoginResponseDTO;
import de.heedlesssoap.geckobackend.entities.DTOs.RegistrationDTO;
import de.heedlesssoap.geckobackend.exceptions.UsernameAlreadyExistsException;
import de.heedlesssoap.geckobackend.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExists() {
        return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFound() {
        return new ResponseEntity<>("Bad Credentials!", HttpStatus.BAD_REQUEST);
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