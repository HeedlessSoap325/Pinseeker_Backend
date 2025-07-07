package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import de.heedlesssoap.pinseekerbackend.entities.Log;
import de.heedlesssoap.pinseekerbackend.entities.enums.LogType;
import de.heedlesssoap.pinseekerbackend.utils.DateUtils;

import java.util.Date;

public class LogDTO {
    private Integer log_id;

    private LogType type;

    private ApplicationUserDTO logger;

    private String message;

    private String created_at;

    public LogDTO() {
    }

    public LogDTO(Integer log_id, LogType type, ApplicationUserDTO logger, String message, String created_at) {
        this.log_id = log_id;
        this.type = type;
        this.logger = logger;
        this.message = message;
        this.created_at = created_at;
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

    public ApplicationUserDTO getLogger() {
        return logger;
    }

    public void setLogger(ApplicationUserDTO logger) {
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

    public LogDTO fromLog(Log log){
        this.log_id = log.getLogId();
        this.type = log.getType();
        this.logger = new ApplicationUserDTO().fromApplicationUser(log.getLogger());
        this.message = log.getMessage();
        this.created_at = DateUtils.formatDate(log.getCreatedAt());
        return this;
    }

    public Log toLog(){
        Log log = new Log();
        log.setLogId(this.log_id);
        log.setType(this.type);
        log.setMessage(this.message);
        log.setCreatedAt(new Date());
        return log;
    }
}