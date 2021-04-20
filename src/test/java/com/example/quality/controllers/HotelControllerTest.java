package com.example.quality.controllers;

import com.example.quality.dtos.BookingRequestDTO;
import com.example.quality.dtos.BookingResponseDTO;
import com.example.quality.dtos.HotelDTO;
import com.example.quality.exceptions.InvalidDateException;
import com.example.quality.exceptions.InvalidLocationException;
import com.example.quality.services.HotelService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class HotelControllerTest {

    @Mock
    HotelService hotelService;

    ObjectMapper objectMapper = new ObjectMapper();

    HotelController hotelController;

    @BeforeEach
    void setup() {
        openMocks(this);
        hotelController = new HotelController(hotelService);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should Return List with Status OK")
    void getAllAvailableHotels() throws Exception {

        List<HotelDTO> hotelList = objectMapper.readValue(
                new File("src/test/resources/mockedAvailableHotels.json"),
                new TypeReference<>() {
                });

        when(hotelService.getAvailableHotels(any())).thenReturn(hotelList);

        ResponseEntity<List<HotelDTO>> result = hotelController.getHotels(new HashMap<>());

        assertEquals(hotelList, result.getBody());
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should Return Filtered List with Status OK")
    void getAvailableHotelsByDateAndDestination() throws Exception {

        List<HotelDTO> hotelList = objectMapper.readValue(
                new File("src/test/resources/mockedFilteredHotels.json"),
                new TypeReference<>() {
                });

        String fromDate = "10/02/2021";
        String toDate = "19/02/2021";
        String destination = "Buenos Aires";

        Map<String, String> params = new HashMap<>();
        params.put("dateFrom", fromDate);
        params.put("dateTo", toDate);
        params.put("destination", destination);

        when(hotelService.getAvailableHotels(any())).thenReturn(hotelList);

        ResponseEntity<List<HotelDTO>> result = hotelController.getHotels(params);

        assertEquals(hotelList, result.getBody());
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should Throw Destination Exception")
    void getFilteredHotelsDestinationException() throws Exception {

        String fromDate = "10/02/2021";
        String toDate = "19/02/2021";
        String destination = "Cancun";

        Map<String, String> params = new HashMap<>();
        params.put("dateFrom", fromDate);
        params.put("dateTo", toDate);
        params.put("destination", destination);

        when(hotelService.getAvailableHotels(any()))
                .thenThrow(new InvalidLocationException("Destination not found"));

        assertThrows(InvalidLocationException.class,
                () -> hotelController.getHotels(params),
                "Destination Exception expected but not thrown");
    }

    @Test
    @DisplayName("Should Throw Date Exception")
    void getFilteredHotelsDateException() throws Exception {

        String fromDate = "20/02/2021";
        String toDate = "19/02/2021";
        String destination = "Buenos Aires";

        Map<String, String> params = new HashMap<>();
        params.put("dateFrom", fromDate);
        params.put("dateTo", toDate);
        params.put("destination", destination);

        when(hotelService.getAvailableHotels(any()))
                .thenThrow(new InvalidDateException("toDate must be greater tha fromDate"));

        assertThrows(InvalidDateException.class,
                () -> hotelController.getHotels(params),
                "Date Exception expected but not thrown");
    }

    @Test
    @DisplayName("Should Return Reservation Status OK")
    void bookHotel() throws Exception {

        BookingRequestDTO request = objectMapper.readValue(
                new File("src/test/resources/mockedBookingRequest.json"),
                new TypeReference<>() {
                });

        BookingResponseDTO response = objectMapper.readValue(
                new File("src/test/resources/mockedBookingResponse.json"),
                new TypeReference<>() {
                });

        when(hotelService.bookARoom(any())).thenReturn(response);

        ResponseEntity<BookingResponseDTO> result = hotelController.bookHotelRoom(request);

        assertEquals(response, result.getBody());
        assertEquals(200, result.getStatusCodeValue());
    }
}