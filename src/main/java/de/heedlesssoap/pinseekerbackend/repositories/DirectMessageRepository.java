package de.heedlesssoap.pinseekerbackend.repositories;

import de.heedlesssoap.pinseekerbackend.entities.Chat;
import de.heedlesssoap.pinseekerbackend.entities.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Integer> {
    @Query("SELECT dm FROM DirectMessage dm " +
            "WHERE dm.chat = :chat AND dm.created_at = (" +
            "SELECT MAX(dm2.created_at) FROM DirectMessage dm2 WHERE dm2.chat = :chat)")
    Optional<DirectMessage> getLastMessageInChat(@Param("chat") Chat chat);
}