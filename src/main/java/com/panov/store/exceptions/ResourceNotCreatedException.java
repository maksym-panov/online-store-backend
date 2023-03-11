package com.panov.store.exceptions;

import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.Map;

@Getter
public class ResourceNotCreatedException extends ResourceException {
    private BindingResult bindingResult;
    private Map<String, String> matches;

    public ResourceNotCreatedException(String message) {
        super(message);
    }

    public ResourceNotCreatedException(BindingResult bindingResult) {
        super();
        this.bindingResult = bindingResult;
    }

    public ResourceNotCreatedException(Map<String, String> matches) {
        super();
        this.matches = matches;
    }
}
