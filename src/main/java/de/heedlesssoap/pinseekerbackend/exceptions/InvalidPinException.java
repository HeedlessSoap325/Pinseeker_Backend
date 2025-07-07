package de.heedlesssoap.pinseekerbackend.exceptions;

public class InvalidPinException extends RuntimeException {
    public InvalidPinException() {
        super("The provided Pin is invalid");
    }
}
