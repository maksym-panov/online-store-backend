package com.panov.store.exceptions;

/**
 * This class is used when an attempt of deleting an entity is failed.
 *
 * @author Maksym Panov
 * @version 1.0
 */
public class ResourceNotDeletedException extends ResourceException {
    public ResourceNotDeletedException(String message) {
        super(message);
    }
}
