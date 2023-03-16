package dev.alpey.reliabill.configuration.exceptions.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.alpey.reliabill.configuration.exceptions.invoice.InvoiceNotFoundException;

@RestControllerAdvice
public class InvoiceExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InvoiceNotFoundException.class)
    public Map<String, String> handleInvoiceNotFoundException(InvoiceNotFoundException ex) {
        return ErrorResponse.getErrorResponse(ex.getMessage());
    }
}
