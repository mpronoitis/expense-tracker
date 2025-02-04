package com.app.expensetracker.error.handler;

import com.app.expensetracker.error.exception.BadUsernameException;
import com.app.expensetracker.shared.rest.enumeration.ErrorType;
import com.app.expensetracker.shared.rest.model.ApiResponse;
import jakarta.validation.constraints.Null;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
}
