package de.heedlesssoap.pinseekerbackend.exceptions;

public class InvalidJWTTokenException extends Exception {
    public InvalidJWTTokenException() {
        super("The provided JWT Token is invalid");
    }
}
