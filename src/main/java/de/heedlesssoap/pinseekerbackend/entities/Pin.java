package de.heedlesssoap.pinseekerbackend.entities;

import de.heedlesssoap.pinseekerbackend.entities.enums.PinSize;
import de.heedlesssoap.pinseekerbackend.entities.enums.PinStatus;
import de.heedlesssoap.pinseekerbackend.entities.enums.PinType;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Pin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pin_id;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private PinType type;

    @Column(nullable = false)
    private Boolean premium;

    @Column(nullable = false)
    private Float difficulty;

    @Column(nullable = false)
    private Float terrain;

    @Enumerated(EnumType.STRING)
    private PinSize size;

    @ManyToOne
    @JoinTable(
            name = "user_pins_junction",
            joinColumns = @JoinColumn(name = "pin_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private ApplicationUser hider;

    @Column(nullable = false)
    private Date created_at;

    private String hint;

    private String description;

    @OneToMany(mappedBy = "parent_pin", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Log> logs;

    @Enumerated(EnumType.STRING)
    private PinStatus status;

    public Pin() {
        this.latitude = 0;
        this.longitude = 0;
        this.name = null;
        this.type = null;
        this.premium = false;
        this.difficulty = null;
        this.terrain = null;
        this.size = null;
        this.created_at = new Date();
        this.hint = null;
        this.description = null;
        this.logs = new HashSet<Log>();
        this.status = PinStatus.ACTIVE;
    }

    public Pin(Integer pin_id, double latitude, double longitude, String name, PinType type, Boolean premium, Float difficulty, Float terrain, PinSize size, ApplicationUser hider, Date created_at, String hint, String description, Set<Log> logs, PinStatus status) {
        this.pin_id = pin_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.type = type;
        this.premium = premium;
        this.difficulty = difficulty;
        this.terrain = terrain;
        this.size = size;
        this.hider = hider;
        this.created_at = created_at;
        this.hint = hint;
        this.description = description;
        this.logs = logs;
        this.status = status;
    }

    public Integer getPinId() {
        return pin_id;
    }

    public void setPinId(Integer pin_id) {
        this.pin_id = pin_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PinType getType() {
        return type;
    }

    public void setType(PinType type) {
        this.type = type;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public Float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Float difficulty) {
        this.difficulty = difficulty;
    }

    public Float getTerrain() {
        return terrain;
    }

    public void setTerrain(Float terrain) {
        this.terrain = terrain;
    }

    public PinSize getSize() {
        return size;
    }

    public void setSize(PinSize size) {
        this.size = size;
    }

    public ApplicationUser getHider() {
        return hider;
    }

    public void setHider(ApplicationUser hider) {
        this.hider = hider;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Log> getLogs() {
        return logs;
    }

    public void setLogs(Set<Log> logs) {
        this.logs = logs;
    }

    public PinStatus getStatus() {
        return status;
    }

    public void setStatus(PinStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Pin{" +
                "pin_id=" + pin_id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", premium=" + premium +
                ", difficulty=" + difficulty +
                ", terrain=" + terrain +
                ", size=" + size +
                ", hider=" + hider +
                ", created_at=" + created_at +
                ", hint='" + hint + '\'' +
                ", description='" + description + '\'' +
                ", logs=" + logs +
                ", status=" + status +
                '}';
    }
}