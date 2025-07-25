package de.heedlesssoap.pinseekerbackend;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.Role;
import de.heedlesssoap.pinseekerbackend.repositories.RoleRepository;
import de.heedlesssoap.pinseekerbackend.repositories.UserRepository;
import de.heedlesssoap.pinseekerbackend.utils.Constants;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class PinSeekerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PinSeekerBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncode){
		return args ->{
			if(roleRepository.findByAuthority("ADMIN").isPresent()) return;
			Role adminRole = roleRepository.save(new Role("ADMIN"));
			roleRepository.save(new Role("USER"));

			if(Constants.IS_IN_DEVELOPMENT) {
				Set<Role> admin_user_roles = new HashSet<>();
				admin_user_roles.add(adminRole);

				//creates a default admin user on startup, this is meant for testing and development only and should be deleted in production
				ApplicationUser admin = new ApplicationUser();
				admin.setUsername(Constants.DEVELOPMENT_DEFAULT_ADMIN_NAME);
				admin.setPassword(passwordEncode.encode(Constants.DEVELOPMENT_DEFAULT_ADMIN_PASSWORD));
				admin.setAuthorities(admin_user_roles);
				admin.setIsPremium(true);
				admin.setIsEnabled(true);

				userRepository.save(admin);
			}
		};
	}
}
