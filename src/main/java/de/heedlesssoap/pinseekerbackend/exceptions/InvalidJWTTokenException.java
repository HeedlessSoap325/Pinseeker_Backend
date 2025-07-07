package de.heedlesssoap.pinseekerbackend.exceptions;

import de.heedlesssoap.pinseekerbackend.utils.Constants;

public class InvalidJWTTokenException extends Exception {
    public InvalidJWTTokenException() {
        super(Constants.INVALID_TOKEN);
    }
}
