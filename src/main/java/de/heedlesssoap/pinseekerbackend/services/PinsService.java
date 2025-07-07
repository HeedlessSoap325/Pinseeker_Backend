package de.heedlesssoap.pinseekerbackend.services;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.PinDTO;
import de.heedlesssoap.pinseekerbackend.entities.Pin;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidPinException;
import de.heedlesssoap.pinseekerbackend.repositories.PinRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class PinsService {
    final PinRepository pinRepository;
    final TokenService tokenService;

    public PinsService(PinRepository pinRepository, TokenService tokenService) {
        this.pinRepository = pinRepository;
        this.tokenService = tokenService;
    }

    private boolean pinValid(Pin pin){
        return pin.getLatitude() <= 180
                && pin.getLatitude() >= -180
                && pin.getLongitude() <= 180
                && pin.getLongitude() >= -180
                && pin.getDifficulty() >= 1
                && pin.getDifficulty() <= 5
                && pin.getTerrain() >= 1
                && pin.getTerrain() <= 5
                && pin.getDifficulty() % 0.5 == 0
                && pin.getTerrain() % 0.5 == 0;
    }

    public ResponseEntity<String> createPin(String token, PinDTO pindto) throws InvalidJWTTokenException, InvalidPinException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin pin = pindto.toPin();

        pin.setPinId(null);
        pin.setCreatedAt(new Date());
        pin.setHider(sender);

        if(!pinValid(pin)){
            throw new InvalidPinException();
        }

        pinRepository.save(pin);
        return new ResponseEntity<>("Pin created successfully", HttpStatus.OK);
    }

    public PinDTO getPin(String token, Integer pinId) throws InvalidJWTTokenException, AccessDeniedException {
        Pin pin =  pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("Pin does not exist"));

        if(pin.getPremium()){
            ApplicationUser sender = tokenService.getSenderFromJWT(token);
            if(!sender.getIsPremium()){
                throw new AccessDeniedException("The requested Pin is premium-only");
            }
        }
        return new PinDTO().fromPin(pin);
    }

    public ResponseEntity<String> editPin(String token, Integer pinId, PinDTO newPinDTO) throws IllegalArgumentException, InvalidJWTTokenException, AccessDeniedException, InvalidPinException {
        Pin editable_pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("Pin does not exist"));
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin new_pin = newPinDTO.toPin();

        if(!editable_pin.getHider().equals(sender)){
            throw new AccessDeniedException("You are not the Owner of this Pin");
        } else if (!pinValid(new_pin)) {
            throw new InvalidPinException();
        }

        new_pin.setHider(sender);
        new_pin.setCreatedAt(editable_pin.getCreatedAt());
        new_pin.setPinId(editable_pin.getPinId());
        pinRepository.save(new_pin);
        return new ResponseEntity<>("Pin updated successfully", HttpStatus.OK);
    }

    public ResponseEntity<String> deletePin(String token, Integer pinId) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(IllegalArgumentException::new);

        if(pin.getHider().equals(sender)){
            pinRepository.delete(pin);
            return new ResponseEntity<>("Pin deleted successfully", HttpStatus.OK);
        }
        throw new AccessDeniedException("You are not the Owner of this Pin");
    }
}