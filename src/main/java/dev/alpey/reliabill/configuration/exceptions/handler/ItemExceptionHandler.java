package dev.alpey.reliabill.configuration.exceptions.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.alpey.reliabill.configuration.exceptions.item.ItemNotFoundException;

@RestControllerAdvice
public class ItemExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ItemNotFoundException.class)
    public Map<String, String> handleItemNotFoundException(ItemNotFoundException ex) {
        return ErrorResponse.getErrorResponse(ex.getMessage());
    }
}
