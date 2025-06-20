package de.heedlesssoap.pinseekerbackend.exceptions;

public class ChatAlreadyExistsException extends RuntimeException {
    public ChatAlreadyExistsException() {
        super("The Chat you tried to create already exists");
    }
}
