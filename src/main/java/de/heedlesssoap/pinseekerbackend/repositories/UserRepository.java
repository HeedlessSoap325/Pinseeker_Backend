package de.heedlesssoap.pinseekerbackend.repositories;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {
    Optional<ApplicationUser> findByUsername(String username);

    Optional<List<ApplicationUser>> findFirst30ByUsernameContainingIgnoreCase(String part);
}