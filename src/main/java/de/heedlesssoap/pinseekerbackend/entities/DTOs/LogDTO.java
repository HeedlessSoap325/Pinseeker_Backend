package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import de.heedlesssoap.pinseekerbackend.entities.Log;
import de.heedlesssoap.pinseekerbackend.entities.enums.LogType;
import de.heedlesssoap.pinseekerbackend.utils.DateUtils;

import java.util.Date;

public class LogDTO {
    private Integer log_id;

    private LogType type;

    private BasicApplicationUserDTO logger;

    private String message;

    private String created_at;

    private Boolean has_image;

    private String imageURL;

    public LogDTO() {
    }

    public LogDTO(Integer log_id, LogType type, BasicApplicationUserDTO logger, String message, String created_at, Boolean has_image, String imageURL) {
        this.log_id = log_id;
        this.type = type;
        this.logger = logger;
        this.message = message;
        this.created_at = created_at;
        this.has_image = has_image;
        this.imageURL = imageURL;
    }

    public Integer getLogId() {
        return log_id;
    }

    public void setLogId(Integer log_id) {
        this.log_id = log_id;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public BasicApplicationUserDTO getLogger() {
        return logger;
    }

    public void setLogger(BasicApplicationUserDTO logger) {
        this.logger = logger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public Boolean getHas_image() {
        return has_image;
    }

    public void setHas_image(Boolean has_image) {
        this.has_image = has_image;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public LogDTO fromLog(Log log, Integer number_of_finds){
        this.log_id = log.getLogId();
        this.type = log.getType();
        this.logger = new BasicApplicationUserDTO().fromApplicationUser(log.getLogger(), number_of_finds);
        this.message = log.getMessage();
        this.created_at = DateUtils.formatDate(log.getCreatedAt());
        this.has_image = log.getHasImage();
        this.imageURL = log.getHasImage() ? log.getImageURL() : null;
        return this;
    }

    public Log toLog(){
        Log log = new Log();
        log.setLogId(this.log_id);
        log.setType(this.type);
        log.setMessage(this.message);
        log.setCreatedAt(new Date());
        log.setHasImage(this.has_image);
        log.setImageURL(this.has_image ? this.imageURL : null);
        return log;
    }
}