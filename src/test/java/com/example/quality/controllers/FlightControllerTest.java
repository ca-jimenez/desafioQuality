package com.example.quality.controllers;

import com.example.quality.dtos.*;
import com.example.quality.exceptions.InvalidDateException;
import com.example.quality.exceptions.InvalidLocationException;
import com.example.quality.services.FlightService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class FlightControllerTest {

    @Mock
    FlightService flightService;

    ObjectMapper objectMapper = new ObjectMapper();

    FlightController flightController;

    @BeforeEach
    void setup() {
        openMocks(this);
        flightController = new FlightController(flightService);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should Return List with Status OK")
    void getAllAvailableFlights() throws Exception {

        List<FlightDTO> flightList = objectMapper.readValue(
                new File("src/test/resources/mockedAvailableFlights.json"),
                new TypeReference<>() {
                });

        when(flightService.getAvailableFlights(any())).thenReturn(flightList);

        ResponseEntity<List<FlightDTO>> result = flightController.getFlights(new HashMap<>());

        assertEquals(flightList, result.getBody());
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should Return Filtered List with Status OK")
    void getAvailableFlightsByDateAndLocation() throws Exception {

        List<FlightDTO> flightList = objectMapper.readValue(
                new File("src/test/resources/mockedFilteredFlights.json"),
                new TypeReference<>() {
                });

        String fromDate = "10/02/2021";
        String toDate = "19/02/2021";
        String origin = "Puerto Iguazu";
        String destination = "Buenos Aires";

        Map<String, String> params = new HashMap<>();
        params.put("dateFrom", fromDate);
        params.put("dateTo", toDate);
        params.put("origin", origin);
        params.put("destination", destination);

        when(flightService.getAvailableFlights(params)).thenReturn(flightList);

        ResponseEntity<List<FlightDTO>> result = flightController.getFlights(params);

        assertEquals(flightList, result.getBody());
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should Throw Destination Exception")
    void getFilteredFlightsDestinationException() throws Exception {

        when(flightService.getAvailableFlights(any()))
                .thenThrow(new InvalidLocationException("Destination not found"));

        assertThrows(InvalidLocationException.class,
                () -> flightController.getFlights(new HashMap<>()),
                "Destination Exception expected but not thrown");
    }

    @Test
    @DisplayName("Should Throw Date Exception")
    void getFilteredFlightsDateException() throws Exception {

        when(flightService.getAvailableFlights(any()))
                .thenThrow(new InvalidDateException("toDate must be greater tha fromDate"));

        assertThrows(InvalidDateException.class,
                () -> flightController.getFlights(new HashMap<>()),
                "Date Exception expected but not thrown");
    }

    @Test
    @DisplayName("Should Return Reservation Status OK")
    void bookFlight() throws Exception {

        FlightReservationRequestDTO request = objectMapper.readValue(
                new File("src/test/resources/mockedReservationRequest.json"),
                new TypeReference<>() {
                });

        FlightReservationResponseDTO response = objectMapper.readValue(
                new File("src/test/resources/mockedReservationResponse.json"),
                new TypeReference<>() {
                });

        when(flightService.bookAFlight(request)).thenReturn(response);

        ResponseEntity<FlightReservationResponseDTO> result = flightController.bookFlight(request);

        assertEquals(response, result.getBody());
        assertEquals(200, result.getStatusCodeValue());
    }
}