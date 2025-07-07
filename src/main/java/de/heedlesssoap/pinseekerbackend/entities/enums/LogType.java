package de.heedlesssoap.pinseekerbackend.entities.enums;

public enum LogType {
    FOUND("Found it"),
    NOT_FOUND("Didn't find it"),
    NOTICE("Write note"),
    MAINTENANCE_REQUIRED("Owner maintenance required"),
    MAINTENANCE_PERFORMED("Owner maintenance performed"),
    DEACTIVATE("Temporarily disabled"),
    ACTIVATE("Enabled");

    final String value;

    LogType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}