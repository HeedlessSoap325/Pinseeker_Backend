package de.heedlesssoap.pinseekerbackend.services;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.BasicApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.LoginResponseDTO;
import de.heedlesssoap.pinseekerbackend.entities.Role;
import de.heedlesssoap.pinseekerbackend.entities.enums.LogType;
import de.heedlesssoap.pinseekerbackend.exceptions.UsernameAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.repositories.LogRepository;
import de.heedlesssoap.pinseekerbackend.repositories.RoleRepository;
import de.heedlesssoap.pinseekerbackend.repositories.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LogRepository logRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, LogRepository logRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.logRepository = logRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public String registerUser(String username, String password) throws UsernameAlreadyExistsException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException();
        }

        String encodedPassword = passwordEncoder.encode(password);

        //This is okay, because our CommandLineRunner created this Role right on Startup
        Role userRole = roleRepository.findByAuthority("USER").get();

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);

        ApplicationUser user = new ApplicationUser();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setAuthorities(userRoles);

        return userRepository.save(user).getUsername();
    }

    public LoginResponseDTO loginUser(String username, String password) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        String token = tokenService.generateJwt(authentication.getName(), (Collection<Role>) authentication.getAuthorities());

        //This is okay, because the authenticationManager would throw an exception if the User wouldn't exist
        //Therefore, because there was no Exception, the User must exist
        ApplicationUser user = userRepository.findByUsername(username).get();
        BasicApplicationUserDTO dto = new BasicApplicationUserDTO().fromApplicationUser(user, logRepository.getNumberOfPinsByLoggerAndType(user, LogType.FOUND));
        return new LoginResponseDTO(dto, token);
    }
}