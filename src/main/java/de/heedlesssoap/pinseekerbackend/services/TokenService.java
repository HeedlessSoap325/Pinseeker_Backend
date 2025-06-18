package de.heedlesssoap.pinseekerbackend.services;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.Role;
import de.heedlesssoap.pinseekerbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private UserRepository userRepository;

    public String generateJwt(String username, Collection<Role> authorities){
        //This is okay, because the AuthenticationManager would throw an Exception, if the User wouldn't exist
        //And therefore, there would never be an Authentication, and this Methode would never be called
        ApplicationUser user = userRepository.findByUsername(username).get();

        Instant now = Instant.now();

        String scope = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(username)
                .claim("roles", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


    public String decodeUsernameFromJWT(String token) {
        token = cleanBearerToken(token);
        return jwtDecoder.decode(token).getSubject();
    }

    public List<Role> decodeRolesFromJWT(String token){
        token = cleanBearerToken(token);

        String scope = jwtDecoder.decode(token).getClaim("roles");
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(scope.split(" "))
                .map(SimpleGrantedAuthority::new)
                .toList();

        return (List<Role>) authorities;
    }

    private String cleanBearerToken(String token){
        return token.replace("Bearer ", "");
    }
}