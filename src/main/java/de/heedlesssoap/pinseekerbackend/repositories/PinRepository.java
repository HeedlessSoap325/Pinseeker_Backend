package de.heedlesssoap.pinseekerbackend.repositories;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.Pin;
import de.heedlesssoap.pinseekerbackend.entities.enums.PinType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PinRepository extends JpaRepository<Pin, Integer> {
    Optional<Pin> findByName(String name);

    @Query("SELECT COUNT(p.pin_id) FROM Pin p " +
            "WHERE p.hider = :user")
    Integer getNumberOfPinsByHider(@Param("user") ApplicationUser user);

    @Query("SELECT p FROM Pin p " +
            "WHERE p.hider = :user")
    List<Pin> getPinsByHider(@Param("user") ApplicationUser hider);

    @Query("SELECT COUNT(p.pin_id) FROM Pin p " +
            "WHERE p.hider = :user AND p.type = :type")
    Integer getNumberOfPinsByHiderAndType(@Param("user") ApplicationUser user, @Param("type") PinType type);
}