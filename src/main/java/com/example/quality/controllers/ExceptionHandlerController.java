package com.example.quality.controllers;

import com.example.quality.dtos.ErrorDTO;
import com.example.quality.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;


@ControllerAdvice(annotations = RestController.class)
public class ExceptionHandlerController {

    private final Integer BAD_REQUEST_STATUS = 400;
    private final Integer INTERNAL_SERVER_ERROR_STATUS = 500;

    @ExceptionHandler(value = {InvalidDateException.class})
    public ResponseEntity<ErrorDTO> InvalidDateExceptionHandler(InvalidDateException e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid Date", e.getMessage(), BAD_REQUEST_STATUS);
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidLocationException.class})
    public ResponseEntity<ErrorDTO> InvalidLocationExceptionHandler(InvalidLocationException e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid Location", e.getMessage(), BAD_REQUEST_STATUS);
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidFilterException.class})
    public ResponseEntity<ErrorDTO> InvalidFilterExceptionHandler(InvalidFilterException e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid filters", e.getMessage(), BAD_REQUEST_STATUS);
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidHotelException.class})
    public ResponseEntity<ErrorDTO> InvalidHotelExceptionHandler(InvalidHotelException e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid Hotel information", e.getMessage(), BAD_REQUEST_STATUS);
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidBookingException.class})
    public ResponseEntity<ErrorDTO> InvalidBookingExceptionHandler(InvalidBookingException e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid booking information", e.getMessage(), BAD_REQUEST_STATUS);
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidPaymentException.class})
    public ResponseEntity<ErrorDTO> InvalidPaymentExceptionHandler(InvalidPaymentException e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid payment method information", e.getMessage(), BAD_REQUEST_STATUS);
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidPersonDataException.class})
    public ResponseEntity<ErrorDTO> InvalidPersonDataExceptionHandler(InvalidPersonDataException e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid user or guest information", e.getMessage(), BAD_REQUEST_STATUS);
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidFlightException.class})
    public ResponseEntity<ErrorDTO> InvalidFlightExceptionHandler(InvalidFlightException e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid flight information", e.getMessage(), BAD_REQUEST_STATUS);
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorDTO> ServerErrorExceptionHandler(Exception e) {
        ErrorDTO errorDTO = new ErrorDTO("There was a problem processing your request", e.getMessage(), INTERNAL_SERVER_ERROR_STATUS);
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
