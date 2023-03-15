package dev.alpey.reliabill.configuration.exceptions.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.alpey.reliabill.configuration.exceptions.company.CompanyNotFoundException;

@RestControllerAdvice
public class CompanyExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CompanyNotFoundException.class)
    public Map<String, String> handleCompanyNotFoundException(CompanyNotFoundException ex) {
        return ErrorResponse.getErrorResponse(ex.getMessage());
    }
}
