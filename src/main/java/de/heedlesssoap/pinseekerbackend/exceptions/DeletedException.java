package de.heedlesssoap.pinseekerbackend.exceptions;

import de.heedlesssoap.pinseekerbackend.utils.Constants;

public class DeletedException extends RuntimeException {
    public DeletedException() {
        super(Constants.USER_DELETED);
    }
}
