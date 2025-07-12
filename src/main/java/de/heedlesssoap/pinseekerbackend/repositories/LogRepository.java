package de.heedlesssoap.pinseekerbackend.repositories;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.Log;
import de.heedlesssoap.pinseekerbackend.entities.Pin;
import de.heedlesssoap.pinseekerbackend.entities.enums.LogType;
import de.heedlesssoap.pinseekerbackend.entities.enums.PinType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Integer> {
    @Query("SELECT COUNT(DISTINCT l.parent_pin) FROM Log l " +
            "WHERE l.logger = :user AND l.type = :type")
    Integer getNumberOfPinsByLoggerAndType(@Param("user") ApplicationUser user,  @Param("type") LogType type);

    @Query("SELECT DISTINCT l.parent_pin FROM Log l "+
            "WHERE l.logger = :user AND l.type = :type")
    List<Pin> getPinsByLoggerAndType(@Param("user") ApplicationUser user, @Param("type") LogType type);

    @Query("SELECT COUNT(DISTINCT l.parent_pin) FROM Log l "+
            "WHERE l.logger = :user AND l.type = :logType AND l.parent_pin.type = :pinType")
    Integer getNumberOfPinsByLoggerAndLogTypeAndPinType(@Param("user") ApplicationUser user, @Param("logType") LogType logType, @Param("pinType") PinType pinType);
}