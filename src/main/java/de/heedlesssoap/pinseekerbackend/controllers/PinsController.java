package de.heedlesssoap.pinseekerbackend.controllers;

import de.heedlesssoap.pinseekerbackend.entities.DTOs.PinDTO;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidPinException;
import de.heedlesssoap.pinseekerbackend.services.PinsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pins")
@CrossOrigin("*")
public class PinsController {
    final PinsService pinsService;

    public PinsController(PinsService pinsService) {
        this.pinsService = pinsService;
    }

    @ExceptionHandler(InvalidJWTTokenException.class)
    public ResponseEntity<String> handleInvalideJWTTokenException(InvalidJWTTokenException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<String> handleInvalidPinException(InvalidPinException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/")
    public ResponseEntity<String> createPin(@RequestHeader("Authorization") String token, @RequestBody PinDTO pindto) throws InvalidJWTTokenException, InvalidPinException {
        return pinsService.createPin(token, pindto);
    }

    @GetMapping("/{pin_id}")
    public PinDTO getPinInfo(@RequestHeader("Authorization") String token, @PathVariable("pin_id") Integer pin_id) throws InvalidJWTTokenException, AccessDeniedException {
        return pinsService.getPin(token, pin_id);
    }

    @PutMapping("/{pin_id}")
    public ResponseEntity<String> editPin(@RequestHeader("Authorization") String token, @PathVariable("pin_id") Integer pin_id, @RequestBody PinDTO new_pindto) throws IllegalArgumentException, InvalidJWTTokenException, AccessDeniedException, InvalidPinException{
        return pinsService.editPin(token, pin_id, new_pindto);
    }

    @DeleteMapping("/{pin_id}")
    public ResponseEntity<String> deletePin(@RequestHeader("Authorization") String token, @PathVariable("pin_id") Integer pin_id) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException{
        return pinsService.deletePin(token, pin_id);
    }
}