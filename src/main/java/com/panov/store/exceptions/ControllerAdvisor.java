package com.panov.store.exceptions;

import com.panov.store.utils.ExceptionBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> resourceNotFoundAdvice(
            ResourceNotFoundException e, WebRequest request) {
        return handleExceptionInternal(e, new ExceptionBody(e.getMessage()),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ResourceNotCreatedException.class)
    public ResponseEntity<Object> resourceNotCreatedAdvice(ResourceNotCreatedException e, WebRequest request) {
        if (e.getBindingResult() != null)
            return handleExceptionInternal(e, fetchErrors(e.getBindingResult()),
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        if (e.getMatches() != null)
            return handleExceptionInternal(e, e.getMatches(),
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        return handleExceptionInternal(e, new ExceptionBody(e.getMessage()),
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ResourceNotUpdatedException.class)
    public ResponseEntity<Object> resourceNotUpdatedAdvice(ResourceNotUpdatedException e, WebRequest request) {
        if (e.getBindingResult() != null)
            return handleExceptionInternal(e, fetchErrors(e.getBindingResult()),
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        if (e.getMatches() != null)
            return handleExceptionInternal(e, e.getMatches(),
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        return handleExceptionInternal(e, new ExceptionBody(e.getMessage()),
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

    }

    @ExceptionHandler(ResourceNotDeletedException.class)
    public ResponseEntity<Object> resourceNotDeletedAdvice(ResourceNotDeletedException e, WebRequest request) {
        return handleExceptionInternal(e, new ExceptionBody(e.getMessage()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private Map<String, String> fetchErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (var e : bindingResult.getFieldErrors())
            errors.put(e.getField(), e.getDefaultMessage());
        return errors;
    }
}
