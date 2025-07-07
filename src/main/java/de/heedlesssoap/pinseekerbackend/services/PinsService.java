package de.heedlesssoap.pinseekerbackend.services;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.LogDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.PinDTO;
import de.heedlesssoap.pinseekerbackend.entities.Log;
import de.heedlesssoap.pinseekerbackend.entities.Pin;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidPinException;
import de.heedlesssoap.pinseekerbackend.repositories.LogRepository;
import de.heedlesssoap.pinseekerbackend.repositories.PinRepository;
import de.heedlesssoap.pinseekerbackend.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PinsService {
    final PinRepository pinRepository;
    final TokenService tokenService;
    final LogRepository logRepository;

    public PinsService(PinRepository pinRepository, TokenService tokenService, LogRepository logRepository) {
        this.pinRepository = pinRepository;
        this.tokenService = tokenService;
        this.logRepository = logRepository;
    }

    private boolean pinValid(Pin pin){
        return pin.getLatitude() <= 90
                && pin.getLatitude() >= -90
                && pin.getLongitude() <= 180
                && pin.getLongitude() >= -180
                && pin.getDifficulty() >= 1
                && pin.getDifficulty() <= 5
                && pin.getTerrain() >= 1
                && pin.getTerrain() <= 5
                && pin.getDifficulty() % 0.5 == 0
                && pin.getTerrain() % 0.5 == 0;
    }

    private Pin getCheckedPin(Integer pin_id) throws IllegalArgumentException{
        return pinRepository.findById(pin_id)
                .orElseThrow(() -> new IllegalArgumentException(Constants.PIN_NOT_FOUND));
    }

    private Log getCheckedLog(Integer log_id) throws IllegalArgumentException{
        return logRepository.findById(log_id)
                .orElseThrow(() -> new IllegalArgumentException(Constants.LOG_NOT_FOUND));
    }

    public ResponseEntity<String> createPin(String token, PinDTO pindto) throws InvalidJWTTokenException, InvalidPinException, IllegalArgumentException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin pin = pindto.toPin();

        if(pinRepository.findByName(pin.getName()).isPresent()){
            throw new IllegalArgumentException(Constants.PIN_ALREADY_EXISTS);
        }

        pin.setPinId(null);
        pin.setCreatedAt(new Date());
        pin.setHider(sender);
        pin.setPremium(sender.getIsPremium() ? pin.getPremium() : false);

        if(!pinValid(pin)){
            throw new InvalidPinException();
        }

        pinRepository.save(pin);
        return new ResponseEntity<>(Constants.ACTION_SUCCESSFUL, HttpStatus.OK);
    }

    public ResponseEntity<PinDTO> getPin(String token, Integer pinId) throws InvalidJWTTokenException, AccessDeniedException {
        Pin pin = getCheckedPin(pinId);

        if(pin.getPremium()){
            ApplicationUser sender = tokenService.getSenderFromJWT(token);
            if(!sender.getIsPremium()){
                throw new AccessDeniedException(Constants.PIN_PREMIUM_ONLY);
            }
        }
        return new ResponseEntity<>(new PinDTO().fromPin(pin), HttpStatus.OK);
    }

    public ResponseEntity<String> editPin(String token, Integer pinId, PinDTO newPinDTO) throws IllegalArgumentException, InvalidJWTTokenException, AccessDeniedException, InvalidPinException {
        Pin editable_pin = getCheckedPin(pinId);
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin new_pin = newPinDTO.toPin();

        if(!editable_pin.getHider().equals(sender)){
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        } else if (!pinValid(new_pin)) {
            throw new InvalidPinException();
        }

        new_pin.setHider(sender);
        new_pin.setCreatedAt(editable_pin.getCreatedAt());
        new_pin.setPinId(editable_pin.getPinId());
        pinRepository.save(new_pin);
        return new ResponseEntity<>(Constants.ACTION_SUCCESSFUL, HttpStatus.OK);
    }

    public ResponseEntity<String> deletePin(String token, Integer pinId) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin pin = getCheckedPin(pinId);

        if(pin.getHider().equals(sender)){
            pinRepository.delete(pin);
            return new ResponseEntity<>(Constants.ACTION_SUCCESSFUL, HttpStatus.OK);
        }
        throw new AccessDeniedException(Constants.ACCESS_DENIED);
    }

    public ResponseEntity<String> createLog(String token, Integer pinId, LogDTO logdto) throws InvalidJWTTokenException, IllegalArgumentException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin parent_pin = getCheckedPin(pinId);
        Log log = logdto.toLog();

        log.setLogId(null);
        log.setLogger(sender);
        log.setParentPin(parent_pin);

        Log created_log = logRepository.save(log);
        parent_pin.getLogs().add(created_log);
        pinRepository.save(parent_pin);

        return new ResponseEntity<>(Constants.ACTION_SUCCESSFUL, HttpStatus.OK);
    }

    public ResponseEntity<List<LogDTO>> getLogs(String token, Integer pinId) throws IllegalArgumentException, InvalidJWTTokenException {
        Pin pin = getCheckedPin(pinId);
        Set<Log> logs = pin.getLogs();

        if(pin.getPremium() && !tokenService.getSenderFromJWT(token).getIsPremium()){
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }

        List<LogDTO> logDTOs = new ArrayList<>();
        logs.forEach((log) -> {
            logDTOs.add(new LogDTO().fromLog(log));
        });

        return new ResponseEntity<>(logDTOs, HttpStatus.OK);
    }

    public ResponseEntity<String> editLog(String token, Integer pinId, Integer logId, LogDTO newlogdto) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin parent_pin = getCheckedPin(pinId);
        Log editable_log = getCheckedLog(logId);
        Log new_log = newlogdto.toLog();

        if(!editable_log.getLogger().equals(sender)){
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }else if (!editable_log.getParentPin().equals(parent_pin)){
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }

        new_log.setLogId(editable_log.getLogId());
        new_log.setLogger(sender);
        new_log.setCreatedAt(editable_log.getCreatedAt());
        new_log.setParentPin(parent_pin);

        logRepository.save(new_log);
        return new ResponseEntity<>(Constants.ACTION_SUCCESSFUL, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteLog(String token, Integer pinId, Integer logId) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin parent_pin = getCheckedPin(pinId);
        Log log = getCheckedLog(logId);

        if(!log.getParentPin().equals(parent_pin)){
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }else if (!log.getLogger().equals(sender)){
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }

        parent_pin.getLogs().remove(log);
        pinRepository.save(parent_pin);

        logRepository.delete(log);
        return new ResponseEntity<>(Constants.ACTION_SUCCESSFUL, HttpStatus.OK);
    }
}