package com.example.quality.handlers;

import com.example.quality.exceptions.InvalidDateException;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateHandlerTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Should Return Parsed Date")
    void parseDate() throws InvalidDateException {
        assertEquals(DateHandler.parseDate("25/01/2020"), LocalDate.parse("2020-01-25"));
    }

    @Test
    @DisplayName("Should Throw Invalid Date Exception")
    void parseInvalidDate() throws InvalidDateException {

        String date = "02/15/2020";

        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> DateHandler.parseDate(date),
                "Invalid Date Exception expected but not thrown");


        assertEquals(exception.getMessage(), "Invalid date format '" + date + "'. Accepted format: DD/MM/YYYY");
    }
}