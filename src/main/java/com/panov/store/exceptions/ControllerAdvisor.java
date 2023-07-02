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

/**
 * This class contains methods that handles exceptions and validation violations <br>
 * and wraps them into JSON messages that are easy to read on the frontend.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see ExceptionBody
 */
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    /**
     * This method catches {@link ResourceNotFoundException} and sends JSON with exception <br>
     * message to the client accompanied by {@code HttpStatus.NOT_FOUND} response code.
     *
     * @param e the caught exception
     * @param request the request that caused exception throwing
     * @return a wrapped exception message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> resourceNotFoundAdvice(
            ResourceNotFoundException e, WebRequest request) {
        return handleExceptionInternal(e, new ExceptionBody(e.getMessage()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * This method catches {@link ResourceNotCreatedException} and sends JSON with exception <br>
     * message or with validation violations of each field to the client accompanied <br>
     * by {@code HttpStatus.BAD_REQUEST} response code.
     *
     * @param e the caught exception
     * @param request the request that caused exception throwing
     * @return a wrapped exception message or validation violations
     */
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

    /**
     * This method catches {@link ResourceNotUpdatedException} and sends JSON with exception <br>
     * message or with validation violations of each field to the client accompanied <br>
     * by {@code HttpStatus.BAD_REQUEST} response code.
     *
     * @param e the caught exception
     * @param request the request that caused exception throwing
     * @return a wrapped exception message or validation violations
     */
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

    /**
     * This method catches {@link ResourceNotDeletedException} and sends JSON with exception <br>
     * message to the client accompanied by {@code HttpStatus.BAD_REQUEST} response code.
     *
     * @param e the caught exception
     * @param request the request that caused exception throwing
     * @return a wrapped exception message
     */
    @ExceptionHandler(ResourceNotDeletedException.class)
    public ResponseEntity<Object> resourceNotDeletedAdvice(ResourceNotDeletedException e, WebRequest request) {
        return handleExceptionInternal(e, new ExceptionBody(e.getMessage()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * This method extracts validation violations from specified {@link BindingResult} <br>
     * and collects them in a {@link Map} object.
     *
     * @param bindingResult Hibernate Validation object that wraps validation violations
     * @return a {@link Map} object with validation violations
     */
    private Map<String, String> fetchErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (var e : bindingResult.getFieldErrors())
            errors.put(e.getField(), e.getDefaultMessage());
        return errors;
    }
}
