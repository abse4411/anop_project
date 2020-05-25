package com.anop.controller;

import com.anop.NotificationCenterApplication;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RemoteExceptionController {
    private static final Logger logger = LoggerFactory.getLogger(RemoteExceptionController.class);

    @ExceptionHandler(FeignException.class)
    public ResponseEntity feignExceptionHandler(FeignException e) {
        ResponseEntity<String> entity = new ResponseEntity(e.contentUTF8(), HttpStatus.valueOf(e.status()));
        logger.error(e.getMessage());
        e.printStackTrace();
        return entity;
    }
}
