package com.anop.controller;

import com.anop.util.BindingResultUtils;
import com.anop.util.JsonResult;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author Xue_Feng
 */
@RestControllerAdvice
public class GlobalExceptionController {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);

    @ExceptionHandler(FeignException.class)
    public ResponseEntity feignExceptionHandler(FeignException e) {
        logger.error(e.getMessage());
        e.printStackTrace();
        if (HttpStatus.resolve(e.status()) != null) {
            return new ResponseEntity<>(e.contentUTF8(), HttpStatus.valueOf(e.status()));
        } else {
            return new ResponseEntity<>(JsonResult.internalServerError(e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(BindException.class)
    public JsonResult bindingExceptionHandler(BindException e, BindingResult bindingResult) {
        logger.error(e.getMessage());
        e.printStackTrace();
        return JsonResult.unprocessableEntity("error in validating", BindingResultUtils.getErrorList(bindingResult));
    }
}
