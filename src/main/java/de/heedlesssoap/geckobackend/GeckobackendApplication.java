package de.heedlesssoap.geckobackend;

import de.heedlesssoap.geckobackend.entities.ApplicationUser;
import de.heedlesssoap.geckobackend.entities.Role;
import de.heedlesssoap.geckobackend.repositories.RoleRepository;
import de.heedlesssoap.geckobackend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class GeckobackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeckobackendApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncode){
		return args ->{
			if(roleRepository.findByAuthority("ADMIN").isPresent()) return;
			Role adminRole = roleRepository.save(new Role("ADMIN"));
			roleRepository.save(new Role("USER"));

			Set<Role> roles = new HashSet<>();
			roles.add(adminRole);

			//creates a default admin user on startup, this is meant for testing and development only and should be deleted in production
			ApplicationUser admin = new ApplicationUser("admin", passwordEncode.encode("password"), roles);

			userRepository.save(admin);
		};
	}
}
