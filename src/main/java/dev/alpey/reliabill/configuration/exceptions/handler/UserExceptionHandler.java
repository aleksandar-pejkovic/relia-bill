package dev.alpey.reliabill.configuration.exceptions.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.alpey.reliabill.configuration.exceptions.user.EmailExistsException;
import dev.alpey.reliabill.configuration.exceptions.user.EmailNotFoundException;
import dev.alpey.reliabill.configuration.exceptions.user.UserNotFoundException;
import dev.alpey.reliabill.configuration.exceptions.user.UsernameExistsException;
import dev.alpey.reliabill.configuration.exceptions.user.UsernameNotFoundException;

@RestControllerAdvice
public class UserExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UsernameExistsException.class)
    public Map<String, String> handleUsernameExistsException(UsernameExistsException ex) {
        return ErrorResponse.getErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public Map<String, String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ErrorResponse.getErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, String> handleUserNotFoundException(UserNotFoundException ex) {
        return ErrorResponse.getErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailExistsException.class)
    public Map<String, String> handleEmailExistsException(EmailExistsException ex) {
        return ErrorResponse.getErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmailNotFoundException.class)
    public Map<String, String> handleEmailNotFoundException(EmailNotFoundException ex) {
        return ErrorResponse.getErrorResponse(ex.getMessage());
    }
}
