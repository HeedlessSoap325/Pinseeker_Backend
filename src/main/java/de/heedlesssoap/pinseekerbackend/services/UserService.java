package de.heedlesssoap.pinseekerbackend.services;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.ExtendedApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.UpdateApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.exceptions.UsernameAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.repositories.LogRepository;
import de.heedlesssoap.pinseekerbackend.repositories.PinRepository;
import de.heedlesssoap.pinseekerbackend.repositories.RoleRepository;
import de.heedlesssoap.pinseekerbackend.repositories.UserRepository;
import de.heedlesssoap.pinseekerbackend.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
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
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogRepository logRepository;
    private final PinRepository pinRepository;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final ImageService imageService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, LogRepository logRepository, PinRepository pinRepository, TokenService tokenService, RoleRepository roleRepository, AuthenticationManager authenticationManager, ImageService imageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.logRepository = logRepository;
        this.pinRepository = pinRepository;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.imageService = imageService;
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

    public ResponseEntity<Map<String, String>> createUser(UpdateApplicationUserDTO updateApplicationUserDTO) {
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
        created_user.setHasProfilePicture(false);
        created_user.setProfilePicture(null);
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
            sender.setUsername(editable_user.getUsername());
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

    public ResponseEntity<ExtendedApplicationUserDTO> getUser(Integer user_id) {
        ApplicationUser requested_user = userRepository.findById(user_id)
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND));

        return new ResponseEntity<>(new ExtendedApplicationUserDTO().fromApplicationUser(requested_user, logRepository, pinRepository), HttpStatus.OK);
    }
}