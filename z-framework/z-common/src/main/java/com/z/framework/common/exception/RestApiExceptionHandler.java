package com.z.framework.common.exception;

import com.z.framework.common.web.rest.vm.ResponseData;
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
        return ResponseData.fail(buffer.toString());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<Object>> handleException(Exception e){
        log.error(e.toString(), e);
        return ResponseData.fail(e.toString());
    }

}
