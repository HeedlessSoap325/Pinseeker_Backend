package de.heedlesssoap.pinseekerbackend.services;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.BasicApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.ExtendedApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.PinDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.UpdateApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.entities.Pin;
import de.heedlesssoap.pinseekerbackend.entities.enums.ChatState;
import de.heedlesssoap.pinseekerbackend.entities.enums.LogType;
import de.heedlesssoap.pinseekerbackend.entities.enums.PinStatus;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.exceptions.UsernameAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.repositories.*;
import de.heedlesssoap.pinseekerbackend.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ChatRepository chatRepository;
    private final LogRepository logRepository;
    private final PinRepository pinRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final TokenService tokenService;

    public UserService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, ChatRepository chatRepository, LogRepository logRepository, PinRepository pinRepository, RoleRepository roleRepository, UserRepository userRepository, ImageService imageService, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.chatRepository = chatRepository;
        this.logRepository = logRepository;
        this.pinRepository = pinRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.tokenService = tokenService;
    }

    private boolean isAuthenticated(String username, String password){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return authentication.isAuthenticated();
    }

    private boolean isPropertyNotUndefined(String property){
        return property != null && !property.isBlank() && !property.isEmpty();
    }

    private void handleAnonymizedUserPins(ApplicationUser anonymized_user){
        pinRepository.getPinsByHider(anonymized_user)
                .orElse(new ArrayList<>())
                .forEach(pin -> {
                    pin.setStatus(PinStatus.ARCHIVED);
                    pinRepository.save(pin);
                });
    }

    private void handleAnonymizedUserChats(ApplicationUser anonymized_user){
        chatRepository.findChatsByParticipants(anonymized_user)
                .orElse(new ArrayList<>())
                .forEach(chat -> {
                    chat.setChatState(ChatState.READ_ONLY);
                    chatRepository.save(chat);
                });
    }

    public ResponseEntity<Map<String, String>> createUser(UpdateApplicationUserDTO updateApplicationUserDTO) {
        //NOTE: this should be prevented by the AuthenticationService, but it's more secure to check anyways
        //NOTE: because a deleted User could get re-enabled by some Bug otherwise.
        if(Constants.isUsernameNotAllowed(updateApplicationUserDTO.getUsername())){
            throw new IllegalArgumentException(Constants.USERNAME_NOT_ACCEPTABLE);
        }
        ApplicationUser user = userRepository.findByUsername(updateApplicationUserDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND));

        if(!passwordEncoder.matches(updateApplicationUserDTO.getPassword(), user.getPassword()) || user.isEnabled()) {
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        } else if (updateApplicationUserDTO.getPublicRsaKey().isBlank()) {
            throw new IllegalArgumentException(Constants.NO_RSA_KEY);
        }
        ApplicationUser created_user = updateApplicationUserDTO.toApplicationUser();

        created_user.setUserId(user.getUserId());
        created_user.setPassword(user.getPassword());
        created_user.setJoinedAt(user.getJoinedAt());
        created_user.setAuthorities(Set.of(roleRepository.findByAuthority("USER").get()));

        created_user.setIsEnabled(true);
        userRepository.save(created_user);
        return new  ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> updateUser(String token, String password, UpdateApplicationUserDTO updateApplicationUserDTO) throws InvalidJWTTokenException, AccessDeniedException, UsernameAlreadyExistsException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        if(!isAuthenticated(sender.getUsername(), password)) {
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }

        ApplicationUser editable_user = updateApplicationUserDTO.toApplicationUser();
        System.out.println(editable_user);
        if(isPropertyNotUndefined(editable_user.getUsername()) && userRepository.findByUsername(editable_user.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException();
        } else if (isPropertyNotUndefined(editable_user.getUsername())) {
            if(Constants.isUsernameNotAllowed(editable_user.getUsername())){
                throw new IllegalArgumentException(Constants.USERNAME_NOT_ACCEPTABLE);
            }else {
                sender.setUsername(editable_user.getUsername());
            }
        }
        if (isPropertyNotUndefined(editable_user.getPassword())) {
            sender.setPassword(passwordEncoder.encode(editable_user.getPassword()));
        }
        if(isPropertyNotUndefined(editable_user.getPublicRSAKey())){
            sender.setPublicRSAKey(editable_user.getPublicRSAKey());
        }
        sender.setProfileLocation(editable_user.getProfileLocation());
        sender.setEmail(editable_user.getEmail());
        sender.setAbout(editable_user.getAbout());
        sender.setIsProfilePrivate(editable_user.getIsProfilePrivate());

        userRepository.save(sender);

        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> updatePicture(String token, MultipartFile image) throws InvalidJWTTokenException, IOException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);

        imageService.updateUserProfilePicture(image, sender);
        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> deletePicture(String token) throws InvalidJWTTokenException, IOException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);

        imageService.deleteProfilePicture(sender);
        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<ExtendedApplicationUserDTO> getUser(String token, Integer user_id) throws InvalidJWTTokenException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        ApplicationUser requested_user = userRepository.findById(user_id)
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND));

        return new ResponseEntity<>(new ExtendedApplicationUserDTO().fromApplicationUser(requested_user, logRepository, pinRepository, requested_user.getIsProfilePrivate() && !requested_user.equals(sender)), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> deleteUser(String token, String password) throws InvalidJWTTokenException, IOException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        if(!isAuthenticated(sender.getUsername(), password)) {
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }

        if(sender.getHasCustomProfilePicture()) {
            imageService.deleteProfilePicture(sender);
        }

        ApplicationUser clean_user = new ApplicationUser();
        clean_user.setUserId(sender.getUserId());
        clean_user.setProfilePicture(Constants.DELETED_PROFILE_PICTURE);

        clean_user.setUsername("DeletedUser@" + UUID.randomUUID());
        clean_user.setPassword("");
        clean_user.setIsEnabled(false);
        clean_user.setIsDeleted(true);
        clean_user.setJoinedAt(new Date(0));

        ApplicationUser anonymized_user = userRepository.save(clean_user);
        handleAnonymizedUserPins(anonymized_user);
        handleAnonymizedUserChats(anonymized_user);

        return new  ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Set<PinDTO>> getFoundPins(String token, Integer user_id) throws InvalidJWTTokenException, UsernameNotFoundException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        ApplicationUser requested_user = userRepository.findById(user_id)
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND));
        if(requested_user.getIsProfilePrivate() && !requested_user.equals(sender)) {
            throw new AccessDeniedException(Constants.USER_PRIVATE);
        }

        List<Pin> found_pins = logRepository.getPinsByLoggerAndType(requested_user, LogType.FOUND).orElse(new ArrayList<>());
        Set<PinDTO> found_pinsDTOs =  found_pins.stream().map(pin -> new PinDTO().fromPin(pin)).collect(Collectors.toSet());

        return new ResponseEntity<>(found_pinsDTOs, HttpStatus.OK);
    }

    public ResponseEntity<Set<PinDTO>> getHiddenPin(String token, Integer user_id) throws InvalidJWTTokenException, UsernameNotFoundException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        ApplicationUser requested_user = userRepository.findById(user_id)
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND));
        if(requested_user.getIsProfilePrivate() && !requested_user.equals(sender)) {
            throw new AccessDeniedException(Constants.USER_PRIVATE);
        }

        List<Pin> hidden_pins = pinRepository.getPinsByHider(requested_user).orElse(new ArrayList<>());
        Set<PinDTO> hidden_pinsDTOs = hidden_pins.stream().map(pin -> new PinDTO().fromPin(pin)).collect(Collectors.toSet());

        return new ResponseEntity<>(hidden_pinsDTOs, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> upgradeUser(String token) throws InvalidJWTTokenException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);

        //NOTE: Normally, there should be some kind of payment or something, but I won't do that,
        //NOTE: as this is just a learning Project and not meant for production or similar.
        sender.setIsPremium(true);
        userRepository.save(sender);

        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Set<BasicApplicationUserDTO>> findUsersByUsername(String token, String username_part) throws InvalidJWTTokenException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);

        List<ApplicationUser> similar_users = userRepository.findFirst30ByUsernameContainingIgnoreCase(username_part)
                .orElse(new ArrayList<>());
        Set<BasicApplicationUserDTO> similar_usersDTOs = similar_users.stream()
                .filter(user -> !user.equals(sender))
                .map(user -> new BasicApplicationUserDTO().fromApplicationUser(user, null))
                .collect(Collectors.toSet());

        return new ResponseEntity<>(similar_usersDTOs, HttpStatus.OK);
    }
}