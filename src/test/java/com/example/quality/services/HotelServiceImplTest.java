package com.example.quality.services;

import com.example.quality.dtos.*;
import com.example.quality.exceptions.InvalidFilterException;
import com.example.quality.exceptions.InvalidHotelException;
import com.example.quality.exceptions.InvalidLocationException;
import com.example.quality.repositories.HotelRepository;
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

import static org.mockito.AdditionalMatchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class HotelServiceImplTest {

    private HotelService hotelService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Mock
    private HotelRepository hotelRepository;

    private static MockedStatic<DateUtil> dateUtil;
    private static MockedStatic<StringUtil> stringUtil;

    @BeforeEach
    void setUp() throws IOException {

        openMocks(this);
        hotelService = new HotelServiceImpl(hotelRepository);

        objectMapper.registerModule(new JavaTimeModule());

        when(hotelRepository.getAvailableHotelsList()).thenReturn(objectMapper.readValue(
                new File("src/test/resources/mockedAvailableHotels.json"),
                new TypeReference<>() {
                }));

        when(hotelRepository.getHotelList()).thenReturn(objectMapper.readValue(
                new File("src/test/resources/mockedHotelList.json"),
                new TypeReference<>() {
                }));

        Mockito.doNothing().when(hotelRepository).reserveHotel(any());
    }


    @Test
    @DisplayName("Should Return All Available Hotels")
    void getAvailableHotels() throws Exception {

        List<HotelDTO> availableHotels = objectMapper.readValue(
                new File("src/test/resources/mockedAvailableHotels.json"),
                new TypeReference<>() {
                });

        assertEquals(availableHotels, hotelService.getAvailableHotels(new HashMap<>()));
    }

    @Test
    @DisplayName("Should Return Filtered Available Hotels")
    void getFilteredAvailableHotels() throws Exception {

        dateUtil = mockStatic(DateUtil.class);
        stringUtil = mockStatic(StringUtil.class);

        Map<String, String> filters = new HashMap<>();

        String fromDate = "10/02/2021";
        String toDate = "19/02/2021";
        String destination = "Buenos Aires";

        filters.put("dateFrom", fromDate);
        filters.put("dateTo", toDate);
        filters.put("destination", destination);

        when(hotelRepository.filterAvailableHotelsByDateAndDestination(LocalDate.parse(fromDate, formatter),
                LocalDate.parse(toDate, formatter), "buenos aires"))
                .thenReturn(objectMapper.readValue(
                        new File("src/test/resources/mockedFilteredHotels.json"),
                        new TypeReference<>() {
                        }));

        dateUtil.when(() -> DateUtil.parseDate(fromDate)).thenReturn(LocalDate.parse(fromDate, formatter));
        dateUtil.when(() -> DateUtil.parseDate(toDate)).thenReturn(LocalDate.parse(toDate, formatter));

        stringUtil.when(() -> StringUtil.normalizeString(destination)).thenReturn(destination.toLowerCase());
        stringUtil.when(() -> StringUtil.normalizeString(not(eq(destination)))).thenReturn("destination");

        List<HotelDTO> availableHotels = objectMapper.readValue(
                new File("src/test/resources/mockedFilteredHotels.json"),
                new TypeReference<>() {
                });

        assertEquals(availableHotels, hotelService.getAvailableHotels(filters));

        dateUtil.close();
        stringUtil.close();
    }

    @Test
    @DisplayName("Should Throw InvalidLocationException")
    void shouldThrowInvalidDestinationException() {

        dateUtil = mockStatic(DateUtil.class);
        stringUtil = mockStatic(StringUtil.class);

        Map<String, String> filters = new HashMap<>();

        String fromDate = "10/02/2021";
        String toDate = "19/02/2021";
        String destination = "Cancunn";

        filters.put("dateFrom", fromDate);
        filters.put("dateTo", toDate);
        filters.put("destination", destination);

        dateUtil.when(() -> DateUtil.parseDate(fromDate)).thenReturn(LocalDate.parse(fromDate, formatter));
        dateUtil.when(() -> DateUtil.parseDate(toDate)).thenReturn(LocalDate.parse(toDate, formatter));

        stringUtil.when(() -> StringUtil.normalizeString(destination)).thenReturn(destination.toLowerCase());
        stringUtil.when(() -> StringUtil.normalizeString(not(eq(destination)))).thenReturn("destination");

        assertThrows(InvalidLocationException.class,
                () -> hotelService.getAvailableHotels(filters),
                "Invalid Destination Exception expected but not thrown");

        dateUtil.close();
        stringUtil.close();
    }

    @Test
    @DisplayName("Should Throw InvalidFilterException")
    void shouldThrowInvalidFilterException() {

        Map<String, String> filters1 = new HashMap<>();
        Map<String, String> filters2 = new HashMap<>();

        String fromDate = "10/02/2021";
        String toDate = "19/02/2021";
        String destination = "Buenos Aires";
        String invalidFilter = "abc";

        filters1.put("dateFrom", fromDate);
        filters1.put("dateTo", toDate);

        filters2.put("dateTo", toDate);
        filters2.put("destination", destination);
        filters2.put("invalidFilter", invalidFilter);

        InvalidFilterException missingFilterE = assertThrows(InvalidFilterException.class,
                () -> hotelService.getAvailableHotels(filters1),
                "Invalid Filter Exception expected but not thrown");

        assertEquals("Accepted filters are 'dateFrom', 'dateTo' and 'destination'", missingFilterE.getMessage());

        InvalidFilterException invalidFilterE = assertThrows(InvalidFilterException.class,
                () -> hotelService.getAvailableHotels(filters2),
                "Invalid Filter Exception expected but not thrown");

        assertEquals("Request with filters must include 'dateFrom', 'dateTo' and 'destination'", invalidFilterE.getMessage());
    }

    @Test
    @DisplayName("Should return Booking Response Status OK")
    void bookARoom() throws Exception {

        BookingRequestDTO request = objectMapper.readValue(
                new File("src/test/resources/mockedBookingRequest.json"),
                new TypeReference<>() {
                });

        when(hotelRepository.getHotelByCode(any())).thenReturn(objectMapper.readValue(
                new File("src/test/resources/mockedHotel.json"),
                new TypeReference<>() {
                }));

        BookingResponseDTO reservation = objectMapper.readValue(
                new File("src/test/resources/mockedBookingResponse.json"),
                new TypeReference<>() {
                });

        assertEquals(reservation, hotelService.bookARoom(request));
    }

    @Test
    @DisplayName("Should throw Invalid Hotel Code exception")
    void bookARoomInvalidCode() throws Exception {

        BookingRequestDTO request = objectMapper.readValue(
                new File("src/test/resources/mockedBookingRequest.json"),
                new TypeReference<>() {
                });

        request.getBooking().setHotelCode("InvalidCode");

        when(hotelRepository.getHotelByCode(any())).thenReturn(null);

        BookingResponseDTO reservation = objectMapper.readValue(
                new File("src/test/resources/mockedBookingResponse.json"),
                new TypeReference<>() {
                });

        InvalidHotelException exception = assertThrows(InvalidHotelException.class,
                () -> hotelService.bookARoom(request),
                "InvalidHotelException expected but not thrown");

        assertEquals("Hotel with code " + request.getBooking().getHotelCode() + " not found",
                exception.getMessage());
    }

    @Test
    @DisplayName("Should throw Invalid Hotel dates exception")
    void bookARoomInvalidDates() throws Exception {

        BookingRequestDTO request = objectMapper.readValue(
                new File("src/test/resources/mockedBookingRequest.json"),
                new TypeReference<>() {
                });

        request.getBooking().setDateTo("15/10/2021");

        when(hotelRepository.getHotelByCode(any())).thenReturn(objectMapper.readValue(
                new File("src/test/resources/mockedHotel.json"),
                new TypeReference<>() {
                }));

        BookingResponseDTO reservation = objectMapper.readValue(
                new File("src/test/resources/mockedBookingResponse.json"),
                new TypeReference<>() {
                });

        InvalidHotelException exception = assertThrows(InvalidHotelException.class,
                () -> hotelService.bookARoom(request),
                "InvalidHotelException expected but not thrown");

        assertEquals("dates not available for hotel " + request.getBooking().getHotelCode(),
                exception.getMessage());
    }

    @Test
    @DisplayName("Should throw Invalid Hotel destination exception")
    void bookARoomInvalidDestination() throws Exception {

        BookingRequestDTO request = objectMapper.readValue(
                new File("src/test/resources/mockedBookingRequest.json"),
                new TypeReference<>() {
                });

        request.getBooking().setDestination("Cancun");

        when(hotelRepository.getHotelByCode(any())).thenReturn(objectMapper.readValue(
                new File("src/test/resources/mockedHotel.json"),
                new TypeReference<>() {
                }));

        BookingResponseDTO reservation = objectMapper.readValue(
                new File("src/test/resources/mockedBookingResponse.json"),
                new TypeReference<>() {
                });

        InvalidHotelException exception = assertThrows(InvalidHotelException.class,
                () -> hotelService.bookARoom(request),
                "InvalidHotelException expected but not thrown");

        assertEquals("Invalid destination for hotel " + request.getBooking().getHotelCode(),
                exception.getMessage());
    }
}