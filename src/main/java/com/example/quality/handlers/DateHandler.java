package com.example.quality.handlers;

import com.example.quality.exceptions.InvalidDateException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateHandler {
    //ToDo change name and package?
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate parseDate(String date) throws InvalidDateException {

        try {
            return LocalDate.parse(date, formatter);

        } catch (Exception e) {

            throw new InvalidDateException("Invalid date format '" + date + "'. Accepted format: DD/MM/YYYY");
        }
    }

//    public static void validateDateRange(LocalDate arrival, LocalDate departure) throws InvalidDateException {
//
//        if (departure.isEqual(arrival) || departure.isBefore(arrival)) {
//            throw new InvalidDateException("dateTo must be greater than dateFrom");
//        }
//    }
}
