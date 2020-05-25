package com.anop.controller;

import com.anop.NotificationCenterApplication;
import com.anop.util.JsonResult;
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
        logger.error(e.getMessage());
        e.printStackTrace();
        if (e.status() > 0) {
            return new ResponseEntity<>(e.contentUTF8(), HttpStatus.valueOf(e.status()));
        } else {
            return new ResponseEntity<>(JsonResult.internalServerError(e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
