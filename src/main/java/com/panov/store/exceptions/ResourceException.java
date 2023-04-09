package com.panov.store.exceptions;

import com.panov.store.model.Product;
import com.panov.store.model.User;

/**
 * The parent for all exceptions, associated with resources, such as {@link User}, {@link Product} etc.
 *
 * @author Maksym Panov
 * @version 1.0
 */
public class ResourceException extends RuntimeException {
    public ResourceException() {}

    public ResourceException(String message) {
        super(message);
    }
}
