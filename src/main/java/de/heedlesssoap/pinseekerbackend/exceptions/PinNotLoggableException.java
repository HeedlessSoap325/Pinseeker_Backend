package de.heedlesssoap.pinseekerbackend.exceptions;

import de.heedlesssoap.pinseekerbackend.utils.Constants;

public class PinNotLoggableException extends RuntimeException {
    public PinNotLoggableException() {
        super(Constants.PIN_NOT_LOGGABLE);
    }
}
