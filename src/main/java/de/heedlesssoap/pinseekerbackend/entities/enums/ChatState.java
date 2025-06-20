package de.heedlesssoap.pinseekerbackend.entities.enums;

public enum ChatState {
    ACTIVE("active"),
    INACTIVE("inactive");

    final String value;

    ChatState(String value) {
        this.value =value;
    }
}