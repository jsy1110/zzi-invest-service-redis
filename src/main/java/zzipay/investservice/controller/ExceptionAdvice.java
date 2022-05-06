package zzipay.investservice.controller;

import zzipay.investservice.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionEntity> exceptionHandler(HttpServletRequest request, final CustomException e) {
        return ResponseEntity
                .status(e.getError().getStatus())
                .body(ExceptionEntity.builder()
                        .errorCode(e.getError().getCode())
                        .errorMessage(e.getError().getMessage() + " " + e.getExtraMessage())
                        .build());
    }
}
