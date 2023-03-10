package com.panov.store.exceptions;

import com.panov.store.exceptions.products.ProductNotCreatedException;
import com.panov.store.exceptions.products.ProductNotDeletedException;
import com.panov.store.exceptions.products.ProductNotFoundException;
import com.panov.store.utils.ExceptionBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.panov.store.exceptions.products.ProductNotUpdatedException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ProductAdvisor extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> productNotFoundAdvice(
            ProductNotFoundException e, WebRequest request) {
        return handleExceptionInternal(e, new ExceptionBody("This product does not exist"),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ProductNotCreatedException.class)
    public ResponseEntity<Object> cannotCreateProductAdvice(ProductNotCreatedException e, WebRequest request) {
        if (e.getBindingResult() == null)
            return handleExceptionInternal(e, new ExceptionBody("Could not create new product"),
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        return handleExceptionInternal(e, fetchErrors(e.getBindingResult()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ProductNotUpdatedException.class)
    public ResponseEntity<Object> cannotUpdateProductAdvice(ProductNotUpdatedException e, WebRequest request) {
        if (e.getBindingResult() == null)
            return handleExceptionInternal(e, new ExceptionBody("Could not update this product"),
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        return handleExceptionInternal(e, fetchErrors(e.getBindingResult()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ProductNotDeletedException.class)
    public ResponseEntity<Object> cannotDeleteProductAdvice(ProductNotDeletedException e, WebRequest request) {
        return handleExceptionInternal(e, new ExceptionBody("Could not delete this product"),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private Map<String, String> fetchErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (var e : bindingResult.getFieldErrors())
            errors.put(e.getField(), e.getDefaultMessage());
        return errors;
    }
}
