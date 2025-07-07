package de.heedlesssoap.pinseekerbackend.entities.enums;

public enum PinType {
    TRADITIONAL("Traditional"),
    MULTI("Multi"),
    MYSTERY("Mystery");

    final String value;
    PinType(final String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}