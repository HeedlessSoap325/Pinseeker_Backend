package de.heedlesssoap.pinseekerbackend.controllers;

import de.heedlesssoap.pinseekerbackend.entities.DTOs.LogDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.PinDTO;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidPinException;
import de.heedlesssoap.pinseekerbackend.services.PinsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pins")
@CrossOrigin("*")
public class PinsController {
    final PinsService pinsService;

    public PinsController(PinsService pinsService) {
        this.pinsService = pinsService;
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, String>> createPin(@RequestHeader("Authorization") String token, @RequestBody PinDTO pindto) throws InvalidJWTTokenException, InvalidPinException, IllegalArgumentException {
        return pinsService.createPin(token, pindto);
    }

    @GetMapping("/{pin_id}")
    public ResponseEntity<PinDTO> getPinInfo(@RequestHeader("Authorization") String token, @PathVariable("pin_id") Integer pin_id) throws InvalidJWTTokenException, AccessDeniedException {
        return pinsService.getPin(token, pin_id);
    }

    @PutMapping("/{pin_id}")
    public ResponseEntity<Map<String, String>> editPin(@RequestHeader("Authorization") String token, @PathVariable("pin_id") Integer pin_id, @RequestBody PinDTO new_pindto) throws IllegalArgumentException, InvalidJWTTokenException, AccessDeniedException, InvalidPinException{
        return pinsService.editPin(token, pin_id, new_pindto);
    }

    @DeleteMapping("/{pin_id}")
    public ResponseEntity<Map<String, String>> deletePin(@RequestHeader("Authorization") String token, @PathVariable("pin_id") Integer pin_id) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException{
        return pinsService.deletePin(token, pin_id);
    }

    @PostMapping("/{pin_id}/logs")
    public ResponseEntity<Map<String, String>> createLog(@RequestHeader("Authorization") String token, @PathVariable("pin_id") Integer pin_id, @RequestBody LogDTO logdto) throws InvalidJWTTokenException, IllegalArgumentException {
        return pinsService.createLog(token, pin_id, logdto);
    }

    @GetMapping("/{pin_id}/logs")
    public ResponseEntity<List<LogDTO>> getLogsForPin(@RequestHeader("Authorization") String token, @PathVariable("pin_id") Integer pin_id) throws IllegalArgumentException, InvalidJWTTokenException{
        return pinsService.getLogs(token, pin_id);
    }

    @PutMapping("/{pin_id}/logs/{log_id}")
    public ResponseEntity<Map<String, String>> editLog(@RequestHeader("Authorization") String token, @PathVariable("pin_id") Integer pin_id, @PathVariable("log_id") Integer log_id , @RequestBody LogDTO new_log) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException{
        return pinsService.editLog(token, pin_id, log_id, new_log);
    }

    @DeleteMapping("/{pin_id}/logs/{log_id}")
    public ResponseEntity<Map<String, String>> deleteLog(@RequestHeader("Authorization") String token, @PathVariable("pin_id") Integer pin_id, @PathVariable("log_id") Integer log_id) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException, IOException {
        return pinsService.deleteLog(token, pin_id, log_id);
    }

    @PutMapping("/{pin_id}/logs/{log_id}/image")
    public ResponseEntity<Map<String, String>> updateImage(@RequestHeader("Authorization") String token, @PathVariable("pin_id") Integer pin_id, @PathVariable("log_id") Integer log_id, @RequestParam(value = "image") MultipartFile image) throws InvalidJWTTokenException, IOException {
        return pinsService.updateImage(token, pin_id, log_id, image);
    }

    @DeleteMapping("/{pin_id}/logs/{log_id}/image")
    public ResponseEntity<Map<String, String>> deleteImage(@RequestHeader("Authorization") String token, @PathVariable("pin_id") Integer pin_id, @PathVariable("log_id") Integer log_id) throws InvalidJWTTokenException, IOException {
        return pinsService.deleteImage(token, pin_id, log_id);
    }
    /**
     @GetMapping("/findNearMe")
     public List<PinDTO> findPinsNearMe(@RequestHeader("Authorization") String token, Location){

     }
     **/
}