package com.github.vssavin.usmancore.exception.event;

/**
 * Special unchecked exception type used to indicate that an error has occurred in an
 * event service.
 *
 * @author vssavin on 01.12.2023.
 */
public class EventServiceException extends RuntimeException {

    public EventServiceException(String message) {
        super(message);
    }

    public EventServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
