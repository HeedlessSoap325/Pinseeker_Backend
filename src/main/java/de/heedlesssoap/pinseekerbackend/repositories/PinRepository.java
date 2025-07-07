package de.heedlesssoap.pinseekerbackend.repositories;

import de.heedlesssoap.pinseekerbackend.entities.Pin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PinRepository extends JpaRepository<Pin, Integer> {
}