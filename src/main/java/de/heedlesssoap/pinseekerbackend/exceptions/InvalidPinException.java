package de.heedlesssoap.pinseekerbackend.exceptions;

import de.heedlesssoap.pinseekerbackend.utils.Constants;

public class InvalidPinException extends RuntimeException {
    public InvalidPinException() {
        super(Constants.INVALID_PIN);
    }
}
