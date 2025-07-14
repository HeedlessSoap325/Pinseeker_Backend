package de.heedlesssoap.pinseekerbackend.exceptions;

import de.heedlesssoap.pinseekerbackend.utils.Constants;

public class ChatNotWritableException extends RuntimeException {
    public ChatNotWritableException() {
        super(Constants.CHAT_NOT_WRITABLE);
    }
}
