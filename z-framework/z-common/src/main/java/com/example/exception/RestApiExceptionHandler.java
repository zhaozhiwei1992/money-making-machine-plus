package com.example.exception;

import com.example.web.rest.vm.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseData<String>> handleConstraintViolationException(ConstraintViolationException cve){
        Set<ConstraintViolation<?>> cves = cve.getConstraintViolations();
        final StringBuilder buffer = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : cves) {
            buffer.append(constraintViolation.getMessage());
            buffer.append("---");
        }
        log.error(buffer.toString());
        final ResponseData<String> responseData = ResponseData.fail(buffer.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<Object>> handleException(Exception e){
        final ResponseData<Object> responseData = ResponseData.fail(e.toString());
        responseData.setData(e.getStackTrace());
        log.error(e.toString(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
    }

}
