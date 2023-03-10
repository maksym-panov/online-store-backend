package com.panov.store.exceptions;

import com.panov.store.exceptions.orders.OrderNotFoundException;
import com.panov.store.utils.ExceptionBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class OrderAdvisor extends ResponseEntityExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Object> orderNotFoundAdvice(OrderNotFoundException e, WebRequest request) {
        return handleExceptionInternal(e, new ExceptionBody("This order does not exist"),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
