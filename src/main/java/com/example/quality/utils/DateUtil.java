package com.example.quality.utils;

import com.example.quality.exceptions.InvalidDateException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate parseDate(String date) throws InvalidDateException {

        try {
            return LocalDate.parse(date, formatter);

        } catch (Exception e) {

            throw new InvalidDateException("Invalid date format '" + date + "'. Accepted format: DD/MM/YYYY");
        }
    }

    public static LocalDateTime parseDateToLocalDateTime(String date) throws InvalidDateException {

        try {
            LocalDate localDate = LocalDate.parse(date, formatter);

            return LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 0, 0, 0);

        } catch (Exception e) {

            throw new InvalidDateException("Invalid date format '" + date + "'. Accepted format: DD/MM/YYYY");
        }
    }

    public static String formatDateToString(LocalDate date) {

        return date.format(formatter);
    }

    public static void validateDateRange(LocalDate fromDate, LocalDate toDate) throws InvalidDateException {

        if (fromDate.compareTo(toDate) >= 0) {
            throw new InvalidDateException("dateTo must be greater than dateFrom");
        }
    }
}
