package de.heedlesssoap.pinseekerbackend.services;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.LogDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.PinDTO;
import de.heedlesssoap.pinseekerbackend.entities.Log;
import de.heedlesssoap.pinseekerbackend.entities.Pin;
import de.heedlesssoap.pinseekerbackend.entities.enums.LogType;
import de.heedlesssoap.pinseekerbackend.entities.enums.PinStatus;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidPinException;
import de.heedlesssoap.pinseekerbackend.exceptions.PinNotLoggableException;
import de.heedlesssoap.pinseekerbackend.repositories.LogRepository;
import de.heedlesssoap.pinseekerbackend.repositories.PinRepository;
import de.heedlesssoap.pinseekerbackend.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PinsService {
    final PinRepository pinRepository;
    final LogRepository logRepository;
    final TokenService tokenService;
    final ImageService imageService;

    public PinsService(PinRepository pinRepository, LogRepository logRepository, TokenService tokenService, ImageService imageService) {
        this.pinRepository = pinRepository;
        this.logRepository = logRepository;
        this.tokenService = tokenService;
        this.imageService = imageService;
    }

    private void checkIsPinNotValid(Pin pin) throws IllegalArgumentException{
        if(pinRepository.findByName(pin.getName()).isPresent() && !Objects.equals(pinRepository.findByName(pin.getName()).get().getPinId(), pin.getPinId())){
            throw new IllegalArgumentException(Constants.PIN_ALREADY_EXISTS);
        }

        if(!(pin.getLatitude() <= 90)
                || !(pin.getLatitude() >= -90)
                || !(pin.getLongitude() <= 180)
                || !(pin.getLongitude() >= -180)
                || pin.getDifficulty() < 1
                || pin.getDifficulty() > 5
                || pin.getTerrain() < 1
                || pin.getTerrain() > 5
                || pin.getDifficulty() % 0.5 != 0
                || pin.getTerrain() % 0.5 != 0
                || pin.getName() == null
                || pin.getName().isBlank()) {
                throw new InvalidPinException();
        }
    }

    private void checkPinPremiumLevel(Pin pin, ApplicationUser user) throws AccessDeniedException {
        if(pin.getPremium() && !user.getIsPremium()){
            throw new AccessDeniedException(Constants.PIN_PREMIUM_ONLY);
        }
    }

    private Pin getCheckedPin(Integer pin_id) throws IllegalArgumentException{
        return pinRepository.findById(pin_id)
                .orElseThrow(() -> new IllegalArgumentException(Constants.PIN_NOT_FOUND));
    }

    private Log getCheckedLog(Integer log_id, Pin parent_pin, ApplicationUser user) throws IllegalArgumentException, AccessDeniedException {
        Log log = logRepository.findById(log_id)
                .orElseThrow(() -> new IllegalArgumentException(Constants.LOG_NOT_FOUND));

        if(!log.getParentPin().equals(parent_pin) || !log.getLogger().equals(user)){
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }

        return log;
    }

    private void changePinStatusOnLog(Log log, Pin pin, ApplicationUser sender) throws AccessDeniedException {
        if(!(log.getType() == LogType.FOUND || log.getType() == LogType.NOT_FOUND || log.getType() == LogType.NOTICE) && !pin.getHider().equals(sender)) {
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }
        switch (log.getType()) {
            case ACTIVATE, MAINTENANCE_PERFORMED -> pin.setStatus(PinStatus.ACTIVE);
            case DEACTIVATE -> pin.setStatus(PinStatus.DEACTIVATED);
            case ARCHIVE -> pin.setStatus(PinStatus.ARCHIVED);
            case MAINTENANCE_REQUIRED -> pin.setStatus(PinStatus.IN_MAINTENANCE);
        }
    }

    public ResponseEntity<Map<String, String>> createPin(String token, PinDTO pindto) throws InvalidJWTTokenException, InvalidPinException, IllegalArgumentException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin pin = pindto.toPin();
        checkIsPinNotValid(pin);

        pin.setPinId(null);
        pin.setCreatedAt(new Date());
        pin.setHider(sender);
        pin.setPremium(sender.getIsPremium() ? pin.getPremium() : false);
        pin.setStatus(PinStatus.ACTIVE);
        pinRepository.save(pin);
        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<PinDTO> getPin(String token, Integer pinId) throws InvalidJWTTokenException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin pin = getCheckedPin(pinId);

        checkPinPremiumLevel(pin, sender);
        return new ResponseEntity<>(new PinDTO().fromPin(pin), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> editPin(String token, Integer pinId, PinDTO newPinDTO) throws IllegalArgumentException, InvalidJWTTokenException, AccessDeniedException, InvalidPinException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin previous_pin = getCheckedPin(pinId);
        Pin new_pin = newPinDTO.toPin();

        if(!previous_pin.getHider().equals(sender)) {
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }
        new_pin.setPinId(previous_pin.getPinId());
        checkIsPinNotValid(new_pin);

        new_pin.setHider(sender);
        new_pin.setPremium(sender.getIsPremium() ? new_pin.getPremium() : false);
        new_pin.setCreatedAt(previous_pin.getCreatedAt());
        pinRepository.save(new_pin);
        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> deletePin(String token, Integer pinId) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin pin = getCheckedPin(pinId);

        if(!pin.getHider().equals(sender)) {
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }
        pin.setStatus(PinStatus.ARCHIVED);
        pinRepository.save(pin);
        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> createLog(String token, Integer pinId, LogDTO logdto) throws InvalidJWTTokenException, IllegalArgumentException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin parent_pin = getCheckedPin(pinId);
        checkPinPremiumLevel(parent_pin, sender);
        if(parent_pin.getStatus() != PinStatus.ACTIVE && !parent_pin.getHider().equals(sender)) {
            throw new PinNotLoggableException();
        }
        Log log = logdto.toLog();

        log.setLogId(null);
        log.setLogger(sender);
        log.setParentPin(parent_pin);
        log.setHasImage(false);
        log.setImageURL(null);

        changePinStatusOnLog(log, parent_pin, sender);

        Log created_log = logRepository.save(log);
        parent_pin.getLogs().add(created_log);
        pinRepository.save(parent_pin);
        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<List<LogDTO>> getLogs(String token, Integer pinId) throws IllegalArgumentException, InvalidJWTTokenException {
        Pin parent_pin = getCheckedPin(pinId);
        checkPinPremiumLevel(parent_pin, tokenService.getSenderFromJWT(token));

        Set<Log> logs = parent_pin.getLogs();
        List<LogDTO> logDTOs = logs.stream().map((log) -> new LogDTO().fromLog(log, logRepository.getNumberOfPinsByLoggerAndType(log.getLogger(), LogType.FOUND))).collect(Collectors.toList());
        return new ResponseEntity<>(logDTOs, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> editLog(String token, Integer pinId, Integer logId, LogDTO newlogdto) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin parent_pin = getCheckedPin(pinId);
        Log previous_log = getCheckedLog(logId, parent_pin, sender);
        Log new_log = newlogdto.toLog();
        changePinStatusOnLog(new_log, parent_pin, sender);

        new_log.setLogId(previous_log.getLogId());
        new_log.setLogger(sender);
        new_log.setCreatedAt(previous_log.getCreatedAt());
        new_log.setParentPin(parent_pin);
        new_log.setHasImage(previous_log.getHasImage());
        new_log.setImageURL(previous_log.getImageURL());

        logRepository.save(new_log);
        pinRepository.save(parent_pin);
        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> deleteLog(String token, Integer pinId, Integer logId) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException, IOException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin parent_pin = getCheckedPin(pinId);
        Log log = getCheckedLog(logId, parent_pin, sender);

        parent_pin.getLogs().remove(log);
        pinRepository.save(parent_pin);

        if(log.getHasImage()){
            imageService.deleteLogImage(log);
        }

        logRepository.delete(log);
        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> updateImage(String token, Integer pin_id, Integer log_id, MultipartFile image) throws InvalidJWTTokenException, IOException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin parent_pin = getCheckedPin(pin_id);
        Log log = getCheckedLog(log_id, parent_pin, sender);

        imageService.updateLogImage(image, log);

        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> deleteImage(String token, Integer pin_id, Integer log_id) throws InvalidJWTTokenException, IOException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Pin parent_pin = getCheckedPin(pin_id);
        Log log = getCheckedLog(log_id, parent_pin, sender);

        imageService.deleteLogImage(log);

        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }
}