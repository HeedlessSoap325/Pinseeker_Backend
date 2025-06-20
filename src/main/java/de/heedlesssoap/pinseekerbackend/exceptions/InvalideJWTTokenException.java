package de.heedlesssoap.pinseekerbackend.exceptions;

public class InvalideJWTTokenException extends Exception {
    public InvalideJWTTokenException() {
        super("The provided JWT Token is invalid");
    }
}
