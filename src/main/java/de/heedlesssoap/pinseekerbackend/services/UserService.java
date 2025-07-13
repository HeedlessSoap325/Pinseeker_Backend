package de.heedlesssoap.pinseekerbackend.services;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.ExtendedApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.UpdateApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.entities.Role;
import de.heedlesssoap.pinseekerbackend.repositories.LogRepository;
import de.heedlesssoap.pinseekerbackend.repositories.PinRepository;
import de.heedlesssoap.pinseekerbackend.repositories.RoleRepository;
import de.heedlesssoap.pinseekerbackend.repositories.UserRepository;
import de.heedlesssoap.pinseekerbackend.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, LogRepository logRepository, PinRepository pinRepository, TokenService tokenService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.logRepository = logRepository;
        this.pinRepository = pinRepository;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<Map<String, String>> createUser(UpdateApplicationUserDTO updateApplicationUserDTO) {
        ApplicationUser user = userRepository.findByUsername(updateApplicationUserDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND));

        if(!passwordEncoder.matches(updateApplicationUserDTO.getPassword(), user.getPassword()) || user.isEnabled()) {
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        } else if (updateApplicationUserDTO.getPublicRSAKey().isBlank()) {
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

    public ResponseEntity<String> updateUser(String token, String password, UpdateApplicationUserDTO updateApplicationUserDTO) {

        return null;
    }

    public ResponseEntity<String> updatePicture(String token, MultipartFile image) {

        return null;
    }

    public ResponseEntity<String> deletePicture(String token) {

        return null;
    }

    public ResponseEntity<ExtendedApplicationUserDTO> getUser(Integer userId) {

        return null;
    }

    public ResponseEntity<String> deleteUser(String token, String password) {

        return null;
    }
}