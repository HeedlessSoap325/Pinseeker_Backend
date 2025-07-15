package de.heedlesssoap.pinseekerbackend.entities.enums;

public enum PinStatus {
    ACTIVE("Active"),
    DEACTIVATED("Deactivated"),
    IN_MAINTENANCE("In maintenance"),
    ARCHIVED("Archived");

    final String value;

    PinStatus(String value) {
        this.value = value;
    }
}
