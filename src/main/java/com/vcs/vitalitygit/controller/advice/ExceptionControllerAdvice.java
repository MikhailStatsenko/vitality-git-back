package com.vcs.vitalitygit.controller.advice;

import com.vcs.vitalitygit.domain.dto.api.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> messages = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            messages.add(errorMessage);
        });
        return new ApiErrorResponse(HttpStatus.BAD_REQUEST, ex, String.join("; ", messages));
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiErrorResponse handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return new ApiErrorResponse(HttpStatus.FORBIDDEN, ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ApiErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }
}