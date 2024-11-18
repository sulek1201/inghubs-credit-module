package com.inghubs.credit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<?> customerNotFound(CustomerNotFoundException customerNotFoundException) {
        List<String> details = new ArrayList<>();
        details.add(customerNotFoundException.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Process is failed", details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CreateLoanException.class)
    public ResponseEntity<?> createLoanFailed(CreateLoanException createLoanException) {
        List<String> details = new ArrayList<>();
        details.add(createLoanException.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Create Loan process is failed", details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<?> getLoanFailed(LoanNotFoundException loanNotFoundException) {
        List<String> details = new ArrayList<>();
        details.add(loanNotFoundException.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Get Loan process is failed", details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
