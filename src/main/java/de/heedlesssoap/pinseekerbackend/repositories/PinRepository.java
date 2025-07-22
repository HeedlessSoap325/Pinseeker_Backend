package de.heedlesssoap.pinseekerbackend.repositories;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.BasicPinDTO;
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
    Optional<List<Pin>> getPinsByHider(@Param("user") ApplicationUser hider);

    @Query("SELECT COUNT(p.pin_id) FROM Pin p " +
            "WHERE p.hider = :user AND p.type = :type")
    Integer getNumberOfPinsByHiderAndType(@Param("user") ApplicationUser user, @Param("type") PinType type);

    /**
     * NOTE: This is very slow and not suitable for production and large Datasets. In PostgreSQl,
     * NOTE: it is faster and better to use an Extension like PostGIS.
     *
     * Step-By-Step Guide for switch:
     *
     * 1. add PostGIS to PostgreSQL:
     *      CREATE EXTENSION IF NOT EXISTS postgis;
     *
     * 2. change the Definition of the coordinates in Pin.java to
     *      @Column(columnDefinition = "geography(Point,4326)")
     *      private Point location;
     *
     * 3. use This Query instead:
     *      @Query(value = """
     *          SELECT * FROM Pin
     *          WHERE ST_DWithin(location, ST_MakePoint(:longitude, :latitude)::geography, :radius)
     *      """, nativeQuery = true)
     *
     * NOTE: Some Properties in the DTOs and Services may need to be changed to adapt to this Change
     **/
    @Query(value = """
        SELECT 
                p.pin_id,
                ST_Y(p.location) AS latitude,
                ST_X(p.location) AS longitude
        FROM pin p
        WHERE ST_DWithin(p.location, ST_MakePoint(:longitude, :latitude)::geography, :radius)
    """, nativeQuery = true)
    Optional<List<BasicPinDTO>> getBasicPinDTOsNearLocation(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("radius") double radius);
}