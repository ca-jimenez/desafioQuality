package com.example.quality.controllers;

import com.example.quality.dtos.ErrorDTO;
import com.example.quality.exceptions.InvalidDateException;
import com.example.quality.exceptions.InvalidDestinationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;


@ControllerAdvice(annotations = RestController.class)
public class ExceptionHandlerController {

    private final Integer BAD_REQUEST_STATUS = 400;
    private final Integer CONFLICT_STATUS = 409;
    private final Integer UNPROCESSABLE_ENTITY_STATUS = 422;
    private final Integer INTERNAL_SERVER_ERROR_STATUS = 500;

    @ExceptionHandler(value = {InvalidDateException.class})
    public ResponseEntity<ErrorDTO> InvalidDateExceptionHandler(InvalidDateException e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid Date", e.getMessage(), BAD_REQUEST_STATUS);
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidDestinationException.class})
    public ResponseEntity<ErrorDTO> InvalidDestinationExceptionHandler(InvalidDestinationException e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid Destination", e.getMessage(), BAD_REQUEST_STATUS);
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(value = {InvalidShopingCartException.class})
//    public ResponseEntity<ErrorDTO> InvalidShopingCartExceptionHandler(InvalidShopingCartException e) {
//        ErrorDTO errorDTO = new ErrorDTO("Invalid Shopping Cart ID", e.getMessage(), BAD_REQUEST_STATUS);
//        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(value = {InvalidClientDateException.class})
//    public ResponseEntity<ErrorDTO> InvalidClientDateExceptionHandler(InvalidClientDateException e) {
//        ErrorDTO errorDTO = new ErrorDTO("Invalid Client information", e.getMessage(), BAD_REQUEST_STATUS);
//        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(value = {EmailConflictException.class})
//    public ResponseEntity<ErrorDTO> EmailConflictExceptionHandler(EmailConflictException e) {
//        ErrorDTO errorDTO = new ErrorDTO("Email address already registered", e.getMessage(), CONFLICT_STATUS);
//        return new ResponseEntity<>(errorDTO, HttpStatus.CONFLICT);
//    }
//
//
//    @ExceptionHandler(value = {InsufficientStockException.class})
//    public ResponseEntity<ErrorDTO> InsufficientStockExceptionHandler(InsufficientStockException e) {
//        ErrorDTO errorDTO = new ErrorDTO("Insufficient Stock", e.getMessage(), UNPROCESSABLE_ENTITY_STATUS);
//        return new ResponseEntity<>(errorDTO, HttpStatus.UNPROCESSABLE_ENTITY);
//    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorDTO> ServerErrorExceptionHandler(Exception e) {
        ErrorDTO errorDTO = new ErrorDTO("There was a problem processing your request", e.getMessage(), INTERNAL_SERVER_ERROR_STATUS);
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
