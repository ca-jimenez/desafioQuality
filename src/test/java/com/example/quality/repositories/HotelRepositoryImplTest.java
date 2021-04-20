package com.example.quality.repositories;

import com.example.quality.dtos.HotelDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HotelRepositoryImplTest {

    private HotelRepository hotelRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

        mapper.registerModule(new JavaTimeModule());
        hotelRepository = new HotelRepositoryImpl("mockedHotelList.csv");
    }

    @Test
    @DisplayName("Should Return Hotel List")
    void getHotelList() throws IOException {

        List<HotelDTO> hotelsList = mapper.readValue(
                new File("src/test/resources/mockedHotelList.json"),
                new TypeReference<>() {
                });

        assertEquals(hotelsList, hotelRepository.getHotelList());
    }

    @Test
    @DisplayName("Should Return Empty List (invalid file path)")
    void failedGetHotelList() {

        hotelRepository = new HotelRepositoryImpl("InvalidPath");

        assertEquals(new ArrayList<>(), hotelRepository.getHotelList());
    }

    @Test
    @DisplayName("Should Return Available Hotels Only")
    void getAvailableHotels() throws IOException {

        List<HotelDTO> availableHotels = mapper.readValue(
                new File("src/test/resources/mockedAvailableHotels.json"),
                new TypeReference<>() {
                });

        assertEquals(availableHotels, hotelRepository.getAvailableHotelsList());
    }

    @Test
    @DisplayName("Should Return Filtered Available Hotels or empty list")
    void getFilteredAvailableHotels() throws Exception {

        LocalDate fromDate = LocalDate.parse("2021-02-10");
        LocalDate toDate = LocalDate.parse("2021-02-19");
        String destination1 = "buenos aires";
        String destination2 = "bogota";

        List<HotelDTO> availableHotels = mapper.readValue(
                new File("src/test/resources/mockedFilteredHotels.json"),
                new TypeReference<>() {
                });

        assertEquals(availableHotels, hotelRepository.filterAvailableHotelsByDateAndDestination(fromDate, toDate, destination1));
        assertEquals(new ArrayList<>(), hotelRepository.filterAvailableHotelsByDateAndDestination(fromDate, toDate, destination2));
    }

    @Test
    @DisplayName("Should Return Hotel by code or null")
    void getHotelByCode() throws Exception {

        String validCode = "BH-0002";
        String invalidCode = "BHC-0002";

        HotelDTO mockHotel = mapper.readValue(
                new File("src/test/resources/mockedHotel.json"),
                new TypeReference<>() {
                });

        assertEquals(mockHotel, hotelRepository.getHotelByCode(validCode));
        assertNull(hotelRepository.getHotelByCode(invalidCode));
    }
}