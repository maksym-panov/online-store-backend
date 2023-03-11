package com.panov.store.exceptions;

import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.Map;

@Getter
public class ResourceNotUpdatedException extends ResourceException {
    private BindingResult bindingResult;
    private Map<String, String> matches;

    public ResourceNotUpdatedException(String message) {
        super(message);
    }

    public ResourceNotUpdatedException(BindingResult bindingResult) {
        super();
        this.bindingResult = bindingResult;
    }

    public ResourceNotUpdatedException(Map<String, String> matches) {
        this.matches = matches;
    }
}
