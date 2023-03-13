package dev.alpey.reliabill.configuration.exceptions.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.alpey.reliabill.configuration.exceptions.product.ProductNotFoundException;

@RestControllerAdvice
public class ProductExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public Map<String, String> handleProductNotFoundException(ProductNotFoundException ex) {
        return ErrorResponse.getErrorResponse(ex.getMessage());
    }
}
