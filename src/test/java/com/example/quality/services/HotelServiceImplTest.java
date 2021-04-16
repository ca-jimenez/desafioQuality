package com.example.quality.services;

import com.example.quality.dtos.HotelDTO;
import com.example.quality.exceptions.InvalidDateException;
import com.example.quality.exceptions.InvalidDestinationException;
import com.example.quality.handlers.StringHandler;
import com.example.quality.repositories.HotelRepository;
import com.example.quality.handlers.DateHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.AdditionalMatchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class HotelServiceImplTest {

    private HotelService hotelService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Mock
    private HotelRepository hotelRepository;

    private static MockedStatic<DateHandler> dateHandler;

    private static MockedStatic<StringHandler> stringHandler;

    @BeforeAll
    static void beforeAll() {
        dateHandler = mockStatic(DateHandler.class);
        stringHandler = mockStatic(StringHandler.class);

    }


    @BeforeEach
    void setUp() throws IOException {
        openMocks(this);
        hotelService = new HotelServiceImpl(hotelRepository);

//        dateHandler = Mockito.mockStatic(DateHandler.class);
//        stringHandler = Mockito.mockStatic(StringHandler.class);

        objectMapper.registerModule(new JavaTimeModule());

        when(hotelRepository.getHotelList()).thenReturn(objectMapper.readValue(
                new File("src/test/resources/mockedHotelList.json"),
                new TypeReference<>() {
                }));
    }

    @AfterAll
    public static void close() {
        dateHandler.close();
        stringHandler.close();
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

        String fromDate = "10/02/2021";
        String toDate = "19/02/2021";
        String destination = "Buenos Aires";

        dateHandler.when(() -> DateHandler.parseDate(fromDate)).thenReturn(LocalDate.parse(fromDate, formatter));
        dateHandler.when(() -> DateHandler.parseDate(toDate)).thenReturn(LocalDate.parse(toDate, formatter));

        stringHandler.when(() -> StringHandler.normalizeString(destination)).thenReturn(destination.toLowerCase());
        stringHandler.when(() -> StringHandler.normalizeString(not(eq(destination)))).thenReturn("destination");

        List<HotelDTO> availableHotels = objectMapper.readValue(
                new File("src/test/resources/mockedFilteredHotels.json"),
                new TypeReference<>() {
                });

        assertEquals(hotelService.getFilteredAvailableHotels(fromDate, toDate, destination), availableHotels);
    }

    @Test
    @DisplayName("Should Throw InvalidDestinationException")
    void shouldThrowInvalidDestinationException() {

        String fromDate = "10/02/2021";
        String toDate = "19/02/2021";
        String destination = "Cancunn";

        dateHandler.when(() -> DateHandler.parseDate(fromDate)).thenReturn(LocalDate.parse(fromDate, formatter));
        dateHandler.when(() -> DateHandler.parseDate(toDate)).thenReturn(LocalDate.parse(toDate, formatter));

        stringHandler.when(() -> StringHandler.normalizeString(destination)).thenReturn(destination.toLowerCase());
        stringHandler.when(() -> StringHandler.normalizeString(not(eq(destination)))).thenReturn("destination");

        assertThrows(InvalidDestinationException.class,
                () -> hotelService.getFilteredAvailableHotels(fromDate, toDate, destination),
                "Invalid Destination Exception expected but not thrown");
    }

//    @Test
//    @DisplayName("Should Throw InvalidDateException: Same Date")
//    void shouldThrowInvalidDateExceptionSameDate() throws Exception {
//
//        InvalidDateException exception = assertThrows(InvalidDateException.class,
//                () -> hotelService.getFilteredAvailableHotels("19/02/2021", "19/02/2021", "Buenos Aires"),
//                "Invalid Date Exception expected but not thrown");
//
//        assertEquals(exception.getMessage(), "dateTo must be greater than dateFrom");
//    }

    @Test
    @DisplayName("Should Throw InvalidDateException: Invalid Range")
    void shouldThrowInvalidDateExceptionInvalidRange() {

        String fromDate = "20/02/2021";
        String toDate = "19/02/2021";
        String destination = "Buenos Aires";

        dateHandler.when(() -> DateHandler.parseDate(fromDate)).thenReturn(LocalDate.parse(fromDate, formatter));
        dateHandler.when(() -> DateHandler.parseDate(toDate)).thenReturn(LocalDate.parse(toDate, formatter));

        stringHandler.when(() -> StringHandler.normalizeString(destination)).thenReturn(destination.toLowerCase());
        stringHandler.when(() -> StringHandler.normalizeString(not(eq(destination)))).thenReturn("destination");

        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> hotelService.getFilteredAvailableHotels(fromDate, toDate, destination),
                "Invalid Date Exception expected but not thrown");

        assertEquals(exception.getMessage(), "dateTo must be greater than dateFrom");
    }

    @Test
    @DisplayName("Should Throw InvalidDateException: Invalid Format")
    void shouldThrowInvalidDateExceptionInvalidFormat() {

        String fromDate = "20-02-2021";
        String toDate = "02/03/2021";
        String destination = "Buenos Aires";

        dateHandler.when(() -> DateHandler.parseDate(fromDate)).thenThrow(new InvalidDateException("Invalid date format '" + fromDate + "'. Accepted format: DD/MM/YYYY"));

        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> hotelService.getFilteredAvailableHotels(fromDate, toDate, destination),
                "Invalid Date Exception expected but not thrown");

        assertEquals(exception.getMessage(), "Invalid date format '" + fromDate + "'. Accepted format: DD/MM/YYYY");
    }
}