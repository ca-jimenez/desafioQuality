package com.example.quality.services;

import com.example.quality.dtos.*;
import com.example.quality.exceptions.InvalidFilterException;
import com.example.quality.exceptions.InvalidFlightException;
import com.example.quality.exceptions.InvalidLocationException;
import com.example.quality.repositories.FlightRepository;
import com.example.quality.utils.DateUtil;
import com.example.quality.utils.StringUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class FlightServiceImplTest {

    private FlightService flightService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Mock
    private FlightRepository flightRepository;

    private static MockedStatic<DateUtil> dateUtil;
    private static MockedStatic<StringUtil> stringUtil;

    @BeforeEach
    void setUp() throws IOException {

        openMocks(this);
        flightService = new FlightServiceImpl(flightRepository);

        objectMapper.registerModule(new JavaTimeModule());

        when(flightRepository.getAvailableFlightsList()).thenReturn(objectMapper.readValue(
                new File("src/test/resources/mockedAvailableFlights.json"),
                new TypeReference<>() {
                }));

        when(flightRepository.getFlightList()).thenReturn(objectMapper.readValue(
                new File("src/test/resources/mockedFlightList.json"),
                new TypeReference<>() {
                }));

        Mockito.doNothing().when(flightRepository).reserveFlight(any(), any());
    }


    @Test
    @DisplayName("Should Return All Available Flights")
    void getAvailableFlights() throws Exception {

        List<FlightDTO> availableFlights = objectMapper.readValue(
                new File("src/test/resources/mockedAvailableFlights.json"),
                new TypeReference<>() {
                });

        assertEquals(availableFlights, flightService.getAvailableFlights(new HashMap<>()));
    }

    @Test
    @DisplayName("Should Return Filtered Available Flights")
    void getFilteredAvailableFlights() throws Exception {

        dateUtil = mockStatic(DateUtil.class);

        Map<String, String> filters = new HashMap<>();

        String fromDate = "10/02/2021";
        String toDate = "19/02/2021";
        String origin = "Puerto Iguazu";
        String destination = "Bogota";

        filters.put("dateFrom", fromDate);
        filters.put("dateTo", toDate);
        filters.put("origin", origin);
        filters.put("destination", destination);

        when(flightRepository.filterAvailableFlightsByDateAndLocation(LocalDate.parse(fromDate, formatter),
                LocalDate.parse(toDate, formatter), "puerto iguazu", "bogota"))
                .thenReturn(objectMapper.readValue(
                        new File("src/test/resources/mockedFilteredFlights.json"),
                        new TypeReference<>() {
                        }));

        dateUtil.when(() -> DateUtil.parseDate(fromDate)).thenReturn(LocalDate.parse(fromDate, formatter));
        dateUtil.when(() -> DateUtil.parseDate(toDate)).thenReturn(LocalDate.parse(toDate, formatter));

        List<FlightDTO> availableFlights = objectMapper.readValue(
                new File("src/test/resources/mockedFilteredFlights.json"),
                new TypeReference<>() {
                });

        assertEquals(availableFlights, flightService.getAvailableFlights(filters));

        dateUtil.close();
    }


    @Test
    @DisplayName("Should Throw InvalidLocationException")
    void shouldThrowInvalidDestinationException() {

        dateUtil = mockStatic(DateUtil.class);

        Map<String, String> filters = new HashMap<>();

        String fromDate = "10/02/2021";
        String toDate = "19/02/2021";
        String origin = "Puerto Iguazu";
        String destination = "Cancunn";

        filters.put("dateFrom", fromDate);
        filters.put("dateTo", toDate);
        filters.put("origin", origin);
        filters.put("destination", destination);

        dateUtil.when(() -> DateUtil.parseDate(fromDate)).thenReturn(LocalDate.parse(fromDate, formatter));
        dateUtil.when(() -> DateUtil.parseDate(toDate)).thenReturn(LocalDate.parse(toDate, formatter));

        assertThrows(InvalidLocationException.class,
                () -> flightService.getAvailableFlights(filters),
                "Invalid Destination Exception expected but not thrown");

        dateUtil.close();
    }


    @Test
    @DisplayName("Should Throw InvalidFilterException")
    void shouldThrowInvalidFilterException() {

        Map<String, String> filters1 = new HashMap<>();
        Map<String, String> filters2 = new HashMap<>();

        String fromDate = "10/02/2021";
        String toDate = "19/02/2021";
        String invalidFilter = "abc";
        String destination = "Buenos Aires";

        filters1.put("dateFrom", fromDate);
        filters1.put("dateTo", toDate);

        filters2.put("dateFrom", fromDate);
        filters2.put("dateTo", toDate);
        filters2.put("destination", destination);
        filters2.put("invalidFilter", invalidFilter);


        InvalidFilterException missingFilterE = assertThrows(InvalidFilterException.class,
                () -> flightService.getAvailableFlights(filters1),
                "Invalid Filter Exception expected but not thrown");

        assertEquals("Accepted filters are 'dateFrom', 'dateTo', 'origin' and 'destination'",
                missingFilterE.getMessage());

        InvalidFilterException extraFilterE = assertThrows(InvalidFilterException.class,
                () -> flightService.getAvailableFlights(filters2),
                "Invalid Filter Exception expected but not thrown");

        assertEquals("Request with filters must include 'dateFrom', 'dateTo', 'origin' and 'destination'",
                extraFilterE.getMessage());
    }


    @Test
    @DisplayName("Should return Reservation Response Status OK")
    void bookAFlight() throws Exception {

        FlightReservationRequestDTO request = objectMapper.readValue(
                new File("src/test/resources/mockedReservationRequest.json"),
                new TypeReference<>() {
                });

        when(flightRepository.getFlightByCode(any())).thenReturn(objectMapper.readValue(
                new File("src/test/resources/mockedFlight.json"),
                new TypeReference<>() {
                }));

        FlightReservationResponseDTO reservation = objectMapper.readValue(
                new File("src/test/resources/mockedReservationResponse.json"),
                new TypeReference<>() {
                });

        assertEquals(reservation, flightService.bookAFlight(request));
    }


    @Test
    @DisplayName("Should throw Invalid Flight Code exception")
    void bookAFlightInvalidCode() throws Exception {

        FlightReservationRequestDTO request = objectMapper.readValue(
                new File("src/test/resources/mockedReservationRequest.json"),
                new TypeReference<>() {
                });

        request.getFlightReservation().setFlightNumber("InvalidCode");

        when(flightRepository.getFlightByCode(any())).thenReturn(null);

        FlightReservationResponseDTO reservation = objectMapper.readValue(
                new File("src/test/resources/mockedReservationResponse.json"),
                new TypeReference<>() {
                });

        InvalidFlightException exception = assertThrows(InvalidFlightException.class,
                () -> flightService.bookAFlight(request),
                "InvalidFlightException expected but not thrown");

        assertEquals("Flight with code " + request.getFlightReservation().getFlightNumber() + " not found",
                exception.getMessage());
    }


    @Test
    @DisplayName("Should throw Invalid Flight dates exception")
    void bookARoomInvalidDates() throws Exception {

        FlightReservationRequestDTO request = objectMapper.readValue(
                new File("src/test/resources/mockedReservationRequest.json"),
                new TypeReference<>() {
                });

        request.getFlightReservation().setDateTo("15/10/2021");

        when(flightRepository.getFlightByCode(any())).thenReturn(objectMapper.readValue(
                new File("src/test/resources/mockedFlight.json"),
                new TypeReference<>() {
                }));

        FlightReservationResponseDTO reservation = objectMapper.readValue(
                new File("src/test/resources/mockedReservationResponse.json"),
                new TypeReference<>() {
                });

        InvalidFlightException exception = assertThrows(InvalidFlightException.class,
                () -> flightService.bookAFlight(request),
                "InvalidFlightException expected but not thrown");

        assertEquals("dates not available for flight " + request.getFlightReservation().getFlightNumber(),
                exception.getMessage());
    }


    @Test
    @DisplayName("Should throw Invalid Flight destination exception")
    void bookARoomInvalidDestination() throws Exception {

        FlightReservationRequestDTO request = objectMapper.readValue(
                new File("src/test/resources/mockedReservationRequest.json"),
                new TypeReference<>() {
                });

        request.getFlightReservation().setDestination("Cancun");

        when(flightRepository.getFlightByCode(any())).thenReturn(objectMapper.readValue(
                new File("src/test/resources/mockedFlight.json"),
                new TypeReference<>() {
                }));

        FlightReservationResponseDTO reservation = objectMapper.readValue(
                new File("src/test/resources/mockedReservationResponse.json"),
                new TypeReference<>() {
                });

        InvalidFlightException exception = assertThrows(InvalidFlightException.class,
                () -> flightService.bookAFlight(request),
                "InvalidFlightException expected but not thrown");

        assertEquals("Invalid destination for flight " + request.getFlightReservation().getFlightNumber(),
                exception.getMessage());
    }
}