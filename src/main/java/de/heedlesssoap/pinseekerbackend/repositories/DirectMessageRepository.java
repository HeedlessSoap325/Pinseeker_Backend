package de.heedlesssoap.pinseekerbackend.repositories;

import de.heedlesssoap.pinseekerbackend.entities.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Integer> {
    @Query("SELECT dm FROM DirectMessage dm " +
            "WHERE dm.chat_id = :chat_id AND dm.created_at = (" +
            "SELECT MAX(dm2.created_at) FROM DirectMessage dm2 WHERE dm2.chat_id = :chat_id)")
    Optional<DirectMessage> getLastMessageInChat(@Param("chat_id") Integer chat_id);
}