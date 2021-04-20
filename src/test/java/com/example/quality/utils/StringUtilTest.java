package com.example.quality.utils;

import com.example.quality.dtos.FlightDTO;
import com.example.quality.dtos.HotelDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should Return Normalized String")
    void normalizeString() {
        assertEquals("puerto iguazu", StringUtil.normalizeString("Puérto Iguazú"));
    }

    @Test
    @DisplayName("Should Return Same String")
    void normalizeStringNoAccents() {
        assertEquals("puerto iguazu", StringUtil.normalizeString("puerto iguazu"));
    }

    @Test
    @DisplayName("Should Return formatted price")
    void formatPrice() {
        assertEquals("$1500", StringUtil.formatPrice(1500));
    }

    @Test
    @DisplayName("Should Return csv hotel row")
    void hotelCsvRow() throws IOException {

        String expected = "BH-0002,Hotel Bristol 2,Buenos Aires,Double,$7200,12/02/2021,17/04/2021,NO";

        HotelDTO hotel = objectMapper.readValue(
                new File("src/test/resources/mockedHotel.json"),
                new TypeReference<>() {
                });

        assertEquals(expected, StringUtil.hotelToCsvRow(hotel));
    }

    @Test
    @DisplayName("Should Return csv flight row")
    void flightCsvRow() throws IOException {

        String expected = "BAPI-1235,Buenos Aires,Puerto Iguazú,Economy,$6500,10/02/2021,15/02/2021,5";

        FlightDTO flight = objectMapper.readValue(
                new File("src/test/resources/mockedFlight.json"),
                new TypeReference<>() {
                });

        assertEquals(expected, StringUtil.flightToCsvRow(flight));
    }
}