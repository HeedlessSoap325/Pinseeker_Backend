package de.heedlesssoap.pinseekerbackend.repositories;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    Optional<List<Chat>> findChatsByParticipantsContaining(Set<ApplicationUser> participants);

    @Query("SELECT COUNT(c) = 1 FROM Chat c " +
            "WHERE :user1 MEMBER OF c.participants AND :user2 MEMBER OF c.participants")
    boolean doesChatWithUserAlreadyExist(@Param("user1") ApplicationUser user1,
                             @Param("user2") ApplicationUser user2);
}