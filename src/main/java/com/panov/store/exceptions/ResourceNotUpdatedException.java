package com.panov.store.exceptions;

import com.panov.store.model.User;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.Map;

/**
 * This class is used when an attempt of updating information of the entity is failed.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Getter
public class ResourceNotUpdatedException extends ResourceException {
    private BindingResult bindingResult;
    private Map<String, String> matches;

    public ResourceNotUpdatedException(String message) {
        super(message);
    }

    /**
     * This constructor is used when the exceptional situation was <br>
     * caused by validation violations.
     *
     * @param bindingResult Hibernate Validation object that wraps validation violations
     */
    public ResourceNotUpdatedException(BindingResult bindingResult) {
        super();
        this.bindingResult = bindingResult;
    }

    /**
     * This constructor is used when the exceptional situation was <br>
     * caused by uniqueness violation (e.g. there is an attempt of changing phone number of a <br>
     * {@link User} to an existing phone number).
     *
     * @param matches {@link Map} of fields that violate uniqueness constraint
     */
    public ResourceNotUpdatedException(Map<String, String> matches) {
        this.matches = matches;
    }
}
