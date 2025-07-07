package de.heedlesssoap.pinseekerbackend.exceptions;

import de.heedlesssoap.pinseekerbackend.utils.Constants;

public class UsernameAlreadyExistsException extends Exception {
    public UsernameAlreadyExistsException(){
        super(Constants.USERNAME_ALREADY_EXISTS);
    }
}