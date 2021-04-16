package com.example.quality.services;

import com.example.quality.dtos.HotelDTO;
import com.example.quality.exceptions.InvalidDateException;
import com.example.quality.exceptions.InvalidDestinationException;
import com.example.quality.repositories.HotelRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.MockitoAnnotations.openMocks;

class HotelServiceImplTest {

    private HotelService hotelService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private HotelRepository hotelRepository;

    @BeforeEach
    void setUp() throws IOException {
        openMocks(this);
        hotelService = new HotelServiceImpl(hotelRepository);

        objectMapper.registerModule(new JavaTimeModule());

        when(hotelRepository.getHotelList()).thenReturn(objectMapper.readValue(
                new File("src/test/resources/mockedHotelList.json"),
                new TypeReference<>() {
                }));
    }

    @Test
    @DisplayName("Should Return Available Hotels Only")
    void getAvailableHotels() throws IOException {

        List<HotelDTO> availableHotels = objectMapper.readValue(
                new File("src/test/resources/mockedAvailableHotels.json"),
                new TypeReference<>() {
                });

        assertEquals(hotelService.getAvailableHotels(), availableHotels);
    }

    @Test
    @DisplayName("Should Return Filtered Available Hotels")
    void getFilteredAvailableHotels() throws Exception {

        List<HotelDTO> availableHotels = objectMapper.readValue(
                new File("src/test/resources/mockedFilteredHotels.json"),
                new TypeReference<>() {
                });

        assertEquals(hotelService.getFilteredAvailableHotels("10/02/2021", "19/02/2021", "Buenos Aires"), availableHotels);
    }

    @Test
    @DisplayName("Should Throw InvalidDestinationException")
    void shouldThrowInvalidDestinationException() throws Exception {

        assertThrows(InvalidDestinationException.class,
                () -> hotelService.getFilteredAvailableHotels("10/02/2021", "19/02/2021", "Cancunn"),
                "Invalid Destination Exception expected but not thrown");
    }

    @Test
    @DisplayName("Should Throw InvalidDateException: Same Date")
    void shouldThrowInvalidDateExceptionSameDate() throws Exception {

        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> hotelService.getFilteredAvailableHotels("19/02/2021", "19/02/2021", "Buenos Aires"),
                "Invalid Date Exception expected but not thrown");

        assertEquals(exception.getMessage(), "dateTo must be greater than dateFrom");
    }

    @Test
    @DisplayName("Should Throw InvalidDateException: Invalid Range")
    void shouldThrowInvalidDateExceptionInvalidRange() throws Exception {

        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> hotelService.getFilteredAvailableHotels("20/02/2021", "19/02/2021", "Buenos Aires"),
                "Invalid Date Exception expected but not thrown");

        assertEquals(exception.getMessage(), "dateTo must be greater than dateFrom");
    }

//    @Test
//    @DisplayName("Should Throw InvalidDateException: Invalid Format")
//    void shouldThrowInvalidDateExceptionInvalidRange() throws Exception {
//
//        InvalidDateException exception = assertThrows(InvalidDateException.class,
//                () -> hotelService.getFilteredAvailableHotels("20/02/2021", "19/02/2021", "Buenos Aires"),
//                "Invalid Date Exception expected but not thrown");
//
//        assertEquals(exception.getMessage(), "dateTo must be greater than dateFrom");
//    }
}