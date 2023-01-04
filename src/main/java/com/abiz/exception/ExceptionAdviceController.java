package com.abiz.exception;

import com.abiz.exception.handler.AccountNumberBlockException;
import com.abiz.exception.handler.InsufficientBalanceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdviceController {

    @ExceptionHandler
    public ResponseEntity handle(AccessDeniedException e) {
        return new ResponseEntity(e.getLocalizedMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity handle(AuthenticationCredentialsNotFoundException e) {
        return new ResponseEntity("Valid authentication was not provided!", HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler
    public ResponseEntity handle(InsufficientBalanceException e) {
        return new ResponseEntity(e.getLocalizedMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler
    public ResponseEntity handle(AccountNumberBlockException e) {
        return new ResponseEntity(e.getLocalizedMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler
    public ResponseEntity handle(MethodArgumentNotValidException e) {
        return new ResponseEntity("negative value is not accepted!", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity handleOthers(Exception e) {
        return new ResponseEntity(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
