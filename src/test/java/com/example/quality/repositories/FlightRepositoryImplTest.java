package com.example.quality.repositories;

import com.example.quality.dtos.FlightDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class FlightRepositoryImplTest {

    private FlightRepository flightRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

        mapper.registerModule(new JavaTimeModule());
        flightRepository = new FlightRepositoryImpl("mockedFlightList.csv");
    }

    @Test
    @DisplayName("Should Return Flights List")
    void getFlightList() throws IOException {

        List<FlightDTO> flightsList = mapper.readValue(
                new File("src/test/resources/mockedFlightList.json"),
                new TypeReference<>() {
                });

        assertEquals(flightsList, flightRepository.getFlightList());
    }

    @Test
    @DisplayName("Should Return Empty List (invalid file path)")
    void failedGetHotelList() {

        flightRepository = new FlightRepositoryImpl("InvalidPath");

        assertEquals(new ArrayList<>(), flightRepository.getFlightList());
    }

    @Test
    @DisplayName("Should Return Available Flights Only")
    void getAvailableFlights() throws IOException {

        List<FlightDTO> availableFlights = mapper.readValue(
                new File("src/test/resources/mockedAvailableFlights.json"),
                new TypeReference<>() {
                });

        assertEquals(availableFlights, flightRepository.getAvailableFlightsList());
    }

    @Test
    @DisplayName("Should Return Filtered Available Flights or empty list")
    void getFilteredAvailableFlights() throws Exception {

        LocalDate fromDate = LocalDate.parse("2021-02-10");
        LocalDate toDate = LocalDate.parse("2021-02-19");
        String origin = "puerto iguazu";
        String destination1 = "bogota";
        String destination2 = "buenos aires";

        List<FlightDTO> availableFlights = mapper.readValue(
                new File("src/test/resources/mockedFilteredFlights.json"),
                new TypeReference<>() {
                });

        assertEquals(availableFlights, flightRepository.filterAvailableFlightsByDateAndLocation(fromDate, toDate, origin, destination1));
        assertEquals(new ArrayList<>(), flightRepository.filterAvailableFlightsByDateAndLocation(fromDate, toDate, origin, destination2));
    }

    @Test
    @DisplayName("Should Return Flight by code or null")
    void getFlightByCode() throws Exception {

        String validCode = "BAPI-1235";
        String invalidCode = "BHC-0342";

        FlightDTO mockFlight = mapper.readValue(
                new File("src/test/resources/mockedFlight.json"),
                new TypeReference<>() {
                });

        assertEquals(mockFlight, flightRepository.getFlightByCode(validCode));
        assertNull(flightRepository.getFlightByCode(invalidCode));
    }
}