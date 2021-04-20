package com.example.quality.utils;

import com.example.quality.exceptions.InvalidDateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    @DisplayName("Should Return Parsed Date")
    void parseDate() throws InvalidDateException {
        assertEquals(LocalDate.parse("2020-01-25"), DateUtil.parseDate("25/01/2020"));
    }

    @Test
    @DisplayName("Should Throw Invalid Date Exception")
    void parseInvalidDate() {

        String date = "02/15/2020";

        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> DateUtil.parseDate(date),
                "Invalid Date Exception expected but not thrown");

        assertEquals("Invalid date format '" + date + "'. Accepted format: DD/MM/YYYY", exception.getMessage());
    }

    @Test
    @DisplayName("Should Return Parsed DateTime")
    void parseDateTime() throws InvalidDateException {

        LocalDateTime expectedDate = LocalDateTime.of(2020, 01, 25, 0, 0, 0);

        assertEquals(expectedDate, DateUtil.parseDateToLocalDateTime("25/01/2020"));
    }

    @Test
    @DisplayName("Should Return formatted Date String")
    void formatDate() {

        LocalDate date = LocalDate.parse("2020-01-25");
        String expectedString = "25/01/2020";

        assertEquals(expectedString, DateUtil.formatDateToString(date));
    }

    @Test
    @DisplayName("Should not throw invalid date range exception")
    void validateRange() {

        LocalDate date1 = LocalDate.parse("2020-01-25");
        LocalDate date2 = LocalDate.parse("2020-01-28");

        assertDoesNotThrow(() -> DateUtil.validateDateRange(date1, date2));
    }

    @Test
    @DisplayName("Should throw invalid date range exception")
    void validateInvalidRange() {

        LocalDate date1 = LocalDate.parse("2020-01-25");
        LocalDate date2 = LocalDate.parse("2020-01-24");
        LocalDate date3 = LocalDate.parse("2020-01-25");

        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> DateUtil.validateDateRange(date1, date2),
                "Invalid date exception expected but not thrown");

        assertEquals("dateTo must be greater than dateFrom",
                exception.getMessage());

        assertThrows(InvalidDateException.class,
                () -> DateUtil.validateDateRange(date1, date3),
                "Invalid date exception expected but not thrown");
    }
}