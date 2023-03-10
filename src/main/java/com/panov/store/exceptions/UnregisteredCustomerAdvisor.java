package com.panov.store.exceptions;

import com.panov.store.exceptions.unregcustomers.UnregisteredCustomerNotFoundException;
import com.panov.store.utils.ExceptionBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class UnregisteredCustomerAdvisor extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UnregisteredCustomerNotFoundException.class)
    public ResponseEntity<Object> unregisteredCustomerNotFoundAdvice(
            UnregisteredCustomerNotFoundException e, WebRequest request) {
        return handleExceptionInternal(e, new ExceptionBody("This unregistered customer does not exist"),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
