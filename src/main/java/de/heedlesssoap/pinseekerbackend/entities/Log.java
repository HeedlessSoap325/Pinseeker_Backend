package de.heedlesssoap.pinseekerbackend.entities;

import de.heedlesssoap.pinseekerbackend.entities.enums.LogType;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer log_id;

    @Enumerated(EnumType.STRING)
    private LogType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_log_junction",
            joinColumns = {@JoinColumn(name = "log_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private ApplicationUser logger;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Date created_at;

    @Column(nullable = false)
    private Boolean hasImage;

    private String imageURL;

    @ManyToOne
    @JoinTable(
            name = "pin_log_junction",
            joinColumns = @JoinColumn(name = "log_id"),
            inverseJoinColumns = @JoinColumn(name = "pin_id")
    )
    private Pin parent_pin;

    public Log() {
        this.created_at = new Date();
        this.hasImage = false;
        this.imageURL = null;
    }

    public Log(Integer log_id, LogType type, ApplicationUser logger, String message, Date created_at, Boolean hasImage, String imageURL, Pin parent_pin) {
        this.log_id = log_id;
        this.type = type;
        this.logger = logger;
        this.message = message;
        this.created_at = created_at;
        this.hasImage = hasImage;
        this.imageURL = imageURL;
        this.parent_pin = parent_pin;
    }

    public Integer getLogId() {
        return log_id;
    }

    public void setLogId(Integer id) {
        this.log_id = id;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public ApplicationUser getLogger() {
        return logger;
    }

    public void setLogger(ApplicationUser logger) {
        this.logger = logger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public Pin getParentPin() {
        return parent_pin;
    }

    public void setParentPin(Pin parent_pin) {
        this.parent_pin = parent_pin;
    }

    public Boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}