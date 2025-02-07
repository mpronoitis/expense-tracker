package com.app.expensetracker.error.handler;

import com.app.expensetracker.error.exception.BadUsernameException;
import com.app.expensetracker.error.exception.GenericBadRequestException;
import com.app.expensetracker.shared.rest.enumeration.ErrorType;
import com.app.expensetracker.shared.rest.model.ApiResponse;
import jakarta.validation.constraints.Null;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

@ControllerAdvice
@Component
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Null> handle(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentExcpetion");

        HashMap<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName =  ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        Set<String> allKeys = errors.keySet();
        Optional<String> optFirstKey = allKeys.stream().findFirst();
//        if (optFirstKey.isPresent() && errors.get(optFirstKey.get()) != null && !errors.get(optFirstKey.get()).isEmpty()) {
//            return new ApiResponse.Builder<Null>(ErrorType.IM_ARGUMENT.getCode(), false).errorMessage(errors.get(optFirstKey.get())).build();
//        }
         return new ApiResponse.Builder<Null>(ErrorType.IM_ARGUMENT.getCode(), false).errorMessage(ErrorType.IM_ARGUMENT.getMessage()).build();
    }

    @ExceptionHandler({BadUsernameException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Null> handle(BadUsernameException ex) {
        log.error("BadUsername exception");
        return new ApiResponse.Builder<Null>(ErrorType.IM_BAD_USERNAME.getCode(),false).errorMessage(ErrorType.IM_BAD_USERNAME.getMessage()).build();
    }

    @ExceptionHandler(GenericBadRequestException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Null> handle(GenericBadRequestException ex) {
        return new ApiResponse.Builder<Null>(ex.getErrorType().getCode(), false).errorMessage(ex.getErrorType().getMessage()).build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Null> handle(BadCredentialsException ex) {
        return new ApiResponse.Builder<Null>(ErrorType.IM_BAD_CREDENTIALS.getCode(), false).errorMessage(ErrorType.IM_BAD_CREDENTIALS.getMessage()).build();
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Null> handle(AuthenticationException ex) {
        return new ApiResponse.Builder<Null>(ErrorType.IM_BAD_CREDENTIALS.getCode(), false).errorMessage(ErrorType.IM_BAD_CREDENTIALS.getMessage()).build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Null> handle(HttpMessageNotReadableException ex) {
        return new ApiResponse.Builder<Null>(ErrorType.IM_GENERIC.getCode(), false).errorMessage(ex.getMessage()).build();
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Null> handle(IllegalArgumentException ex) {
        return new ApiResponse.Builder<Null>(ErrorType.IM_GENERIC.getCode(), false).errorMessage(ex.getMessage()).build();
    }
}
