package de.heedlesssoap.pinseekerbackend.controllers;

import de.heedlesssoap.pinseekerbackend.exceptions.ChatAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidPinException;
import de.heedlesssoap.pinseekerbackend.exceptions.UsernameAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException exception) {
        return new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        return new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, String>> handleDisabledException() {
        return new ResponseEntity<>(Map.of("error", Constants.USER_NOT_ENABLED), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleCredentialException() {
        return new ResponseEntity<>(Map.of("error", Constants.BAD_CREDENTIALS), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidJWTTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalideJWTTokenException(InvalidJWTTokenException exception){
        return new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException exception){
        return new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ChatAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleChatAlreadyExistsException(ChatAlreadyExistsException exception){
        return new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPinException(InvalidPinException exception){
        return new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException exception){
        return new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleIOException(IOException exception){
        return new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleFileAlreadyExistsException(){
        return new ResponseEntity<>(Map.of("error", Constants.FILE_ALREADY_EXISTS), HttpStatus.CONFLICT);
    }
}