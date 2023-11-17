package com.talk.notificationservice.exception;

import com.talk.notificationservice.dto.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {ApplicationException.class})
    public ResponseEntity<CommonResponse> handleException(ApplicationException e) {
        return ResponseEntity.ok(CommonResponse.error(e.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse> handleException(MethodArgumentNotValidException e) {
        // return the default message
        return ResponseEntity.ok(CommonResponse.error(e.getBindingResult().getFieldError().getDefaultMessage()));
    }
}
