package com.example.adconsumer.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public void ExHandler(Exception e) {
        log.error(e.toString());
        e.printStackTrace();

    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<RestApiException> ExHandler(Exception exception) {
//        log.error(exception.toString());
//        exception.printStackTrace();
//
//        RestApiException restApiException = new RestApiException(
//            exception.getMessage(),
//            HttpStatus.BAD_REQUEST.value());
//
//        return new ResponseEntity<>(
//            restApiException,
//            HttpStatus.BAD_REQUEST
//        );
//    }
}
