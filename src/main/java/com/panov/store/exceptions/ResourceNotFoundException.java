package com.panov.store.exceptions;

/**
 * This class is used when an attempt to find an entity has thrown an exception.
 *
 * @author Maksym Panov
 * @version 1.0
 */
public class ResourceNotFoundException extends ResourceException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
