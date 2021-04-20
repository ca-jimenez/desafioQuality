package com.example.quality.services;


import com.example.quality.dtos.*;
import com.example.quality.exceptions.*;
import com.example.quality.repositories.HotelRepository;
import com.example.quality.utils.BookingUtil;
import com.example.quality.utils.DateUtil;
import com.example.quality.utils.PersonValidationUtil;
import com.example.quality.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public List<HotelDTO> getAvailableHotels(Map<String, String> params) throws Exception {

        // Get hotels with reserved = false
        List<HotelDTO> allAvailableHotels = hotelRepository.getAvailableHotelsList();

        if (params.size() < 1) {
            // Return hotel list
            return allAvailableHotels;

        } else if (params.size() == 3) {

            // validate filters and get filtered hotels
            validateRequiredFilters(params);
            return getFilteredHotels(params);

        } else {
            throw new InvalidFilterException("Accepted filters are 'dateFrom', 'dateTo' and 'destination'");
        }
    }

    @Override
    public BookingResponseDTO bookARoom(BookingRequestDTO request) throws Exception {

        // validate username is valid email
        PersonValidationUtil.validateEmail(request.getUsername());

        BookingDTO booking = request.getBooking();

        // find requested hotel by hotelCode
        HotelDTO hotelRoom = hotelRepository.getHotelByCode(booking.getHotelCode());

        //validate booking contents
        validateHotel(hotelRoom, booking);
        validatePeopleAmount(booking.getRoomType().toLowerCase(), booking.getPeopleAmount(), booking.getPeople().size());
        PersonValidationUtil.validatePeopleData(booking.getPeople());

        // calculate price, interests and total
        Double amount = calculateHotelBaseAmount(hotelRoom, booking);
        Integer interest = BookingUtil.getInterestPercentage(booking.getPaymentMethod());
        Double amountWithInterests = BookingUtil.calculateTotalWithInterests(amount, interest);

        // save to database
        hotelRepository.reserveHotel(booking.getHotelCode());

        return new BookingResponseDTO(
                request.getUsername(),
                amount,
                interest,
                amountWithInterests,
                booking,
                new StatusDTO(200, "Room booked successfully")
        );
    }


    private Double calculateHotelBaseAmount(HotelDTO hotelRoom, BookingDTO booking) throws InvalidDateException {

        // parse dates to LocalDateTime to be able to calculate period
        LocalDateTime dateFrom = DateUtil.parseDateToLocalDateTime(booking.getDateFrom());
        LocalDateTime dateTo = DateUtil.parseDateToLocalDateTime(booking.getDateTo());

        // calculate number of nights
        long days = Duration.between(dateFrom, dateTo).toDays();

        return hotelRoom.getPricePerNight() * (double) days;
    }


    private void validateHotel(HotelDTO hotelRoom, BookingDTO booking) throws Exception {

        // validate hotel exists
        if (hotelRoom == null) {
            throw new InvalidHotelException("Hotel with code " + booking.getHotelCode() + " not found");
        }

        // validate hotel is available
        if (hotelRoom.getReserved()) {
            throw new InvalidHotelException("Hotel with code " + booking.getHotelCode() + " is not available");
        }

        // validate room type matches
        if (!hotelRoom.getRoomType().equalsIgnoreCase(booking.getRoomType())) {
            throw new InvalidHotelException("Invalid room type for hotel " + booking.getHotelCode());
        }

        // replace accents and Uppercase characters
        String normalizedDestination = StringUtil.normalizeString(booking.getDestination());

        // validate destination matches
        if (!StringUtil.normalizeString(hotelRoom.getCity()).equals(normalizedDestination)) {
            throw new InvalidHotelException("Invalid destination for hotel " + booking.getHotelCode());
        }

        // parse and validate date strings are valid dates
        LocalDate dateFrom = DateUtil.parseDate(booking.getDateFrom());
        LocalDate dateTo = DateUtil.parseDate(booking.getDateTo());

        // validate dates don't overlap
        DateUtil.validateDateRange(dateFrom, dateTo);

        // validate hotel is available for requested dates
        if (hotelRoom.getAvailableFrom().compareTo(dateFrom) > 0
                || hotelRoom.getAvailableTo().compareTo(dateTo) < 0) {
            throw new InvalidHotelException("dates not available for hotel " + booking.getHotelCode());
        }
    }

    private void validatePeopleAmount(String roomType, Integer peopleAmount, Integer peopleListSize) throws InvalidBookingException {

        boolean isValidAmountOfPeople;

        switch (roomType) {
            case "single":
                isValidAmountOfPeople = peopleAmount == 1;
                break;
            case "double":
                isValidAmountOfPeople = peopleAmount == 2;
                break;
            case "triple":
                isValidAmountOfPeople = peopleAmount == 3;
                break;
            case "multiple":
                isValidAmountOfPeople = peopleAmount > 3 && peopleAmount <= 10;
                break;
            default:
                isValidAmountOfPeople = false;
        }

        // validate amount of people matches room type and people list size
        if (!isValidAmountOfPeople || !peopleAmount.equals(peopleListSize)) {
            throw new InvalidBookingException("Room type does not match amount of people");
        }
    }

    private List<HotelDTO> getFilteredHotels(Map<String, String> filters) throws Exception {

        // parse and validate date strings
        LocalDate fromDate = DateUtil.parseDate(filters.get("dateFrom"));
        LocalDate toDate = DateUtil.parseDate(filters.get("dateTo"));

        // validate dates don't overlap
        DateUtil.validateDateRange(fromDate, toDate);

        // replace accents and uppercase characters
        String normalizedDestination = StringUtil.normalizeString(filters.get("destination"));

        //validate destination exists
        validateDestination(normalizedDestination);

        return hotelRepository.filterAvailableHotelsByDateAndDestination(fromDate, toDate, normalizedDestination);
    }

    private void validateDestination(String destination) throws InvalidLocationException {

        // search destination in complete hotel list (reserved false and true)
        Optional<HotelDTO> hotelByLocation = hotelRepository.getHotelList().stream()
                .filter(hotel -> StringUtil.normalizeString(hotel.getCity()).equals(destination))
                .findAny();

        if (hotelByLocation.isEmpty()) {
            throw new InvalidLocationException("Destination not found");
        }
    }

    private void validateRequiredFilters(Map<String, String> filters) throws InvalidFilterException {

        // validate that the three required filters are sent
        if (filters.get("dateFrom") == null
                || filters.get("dateTo") == null
                || filters.get("destination") == null) {
            throw new InvalidFilterException("Request with filters must include 'dateFrom', 'dateTo' and 'destination'");
        }
    }
}
