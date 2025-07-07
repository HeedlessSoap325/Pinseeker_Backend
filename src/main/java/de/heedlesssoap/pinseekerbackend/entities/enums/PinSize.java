package de.heedlesssoap.pinseekerbackend.entities.enums;

public enum PinSize {
    MICRO("Micro"),
    SMALL("Small"),
    REGULAR("Regular"),
    LARGE("Large"),
    OTHER("Other");

    final String value;

    PinSize(final String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
