package de.heedlesssoap.pinseekerbackend.services;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.LoginResponseDTO;
import de.heedlesssoap.pinseekerbackend.entities.Role;
import de.heedlesssoap.pinseekerbackend.exceptions.UsernameAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.repositories.RoleRepository;
import de.heedlesssoap.pinseekerbackend.repositories.UserRepository;

import de.heedlesssoap.pinseekerbackend.utils.Constants;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

        return userRepository.save(new ApplicationUser(username, encodedPassword, userRoles, false)).getUsername();
    }

    public LoginResponseDTO loginUser(String username, String password) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = tokenService.generateJwt(authentication.getName(), (Collection<Role>) authentication.getAuthorities());

            //This is okay, because the authenticationManager would throw an exception if the User wouldn't exist
            //Therefore, because there was no Exception, the User must exist
            return new LoginResponseDTO(userRepository.findByUsername(username).get().getUsername(), token);
        }catch (AuthenticationException e){
            throw new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND);
        }
    }
}