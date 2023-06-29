package dev.alpey.reliabill.configuration.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.alpey.reliabill.configuration.exceptions.passwordResetToken.PasswordResetTokenExpiredException;
import dev.alpey.reliabill.configuration.exceptions.passwordResetToken.PasswordResetTokenNotFoundException;

@RestControllerAdvice
public class PasswordResetTokenExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PasswordResetTokenNotFoundException.class)
    public String handlePasswordResetTokenNotFoundException(PasswordResetTokenNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordResetTokenExpiredException.class)
    public String handlePasswordResetTokenExpiredException(PasswordResetTokenExpiredException ex) {
        return ex.getMessage();
    }
}
