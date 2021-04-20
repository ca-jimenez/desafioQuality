package com.example.quality.services;


import com.example.quality.dtos.*;
import com.example.quality.exceptions.*;
import com.example.quality.repositories.FlightRepository;
import com.example.quality.utils.BookingUtil;
import com.example.quality.utils.DateUtil;
import com.example.quality.utils.PersonValidationUtil;
import com.example.quality.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }


    @Override
    public List<FlightDTO> getAvailableFlights(Map<String, String> params) throws Exception {

        //get list of available flights
        List<FlightDTO> allAvailableHotels = flightRepository.getAvailableFlightsList();

        if (params.size() < 1) {

            // return flight list
            return allAvailableHotels;

        } else if (params.size() == 4) {

            // validate parameters and get filtered flights
            validateRequiredFilters(params);
            return getFilteredFlights(params);

        } else {
            throw new InvalidFilterException("Accepted filters are 'dateFrom', 'dateTo', 'origin' and 'destination'");
        }
    }

    @Override
    public FlightReservationResponseDTO bookAFlight(FlightReservationRequestDTO request) throws Exception {

        // validate username is valid email
        PersonValidationUtil.validateEmail(request.getUsername());

        FlightReservationDTO reservation = request.getFlightReservation();

        // find requested flight by flightNumber
        FlightDTO flight = flightRepository.getFlightByCode(reservation.getFlightNumber());

        // validate reservation contents
        validateFlight(flight, reservation);
        validatePeopleAmount(reservation.getSeats(), reservation.getPeople().size());
        PersonValidationUtil.validatePeopleData(reservation.getPeople());

        // calculate price, interest and total
        Double amount = calculateFlightBaseAmount(flight, reservation);
        Integer interest = BookingUtil.getInterestPercentage(reservation.getPaymentMethod());
        Double amountWithInterests = BookingUtil.calculateTotalWithInterests(amount, interest);

        // save to database
        flightRepository.reserveFlight(reservation.getFlightNumber(), reservation.getSeats());

        return new FlightReservationResponseDTO(
                request.getUsername(),
                amount,
                interest,
                amountWithInterests,
                reservation,
                new StatusDTO(200, "Flight booked successfully")
        );
    }


    private Double calculateFlightBaseAmount(FlightDTO flight, FlightReservationDTO reservation) {

        return flight.getPricePerPerson() * (double) reservation.getSeats();
    }


    private void validateFlight(FlightDTO flight, FlightReservationDTO reservation) throws Exception {

        // validate flight exists
        if (flight == null) {
            throw new InvalidFlightException("Flight with code " + reservation.getFlightNumber() + " not found");
        }

        // validate flight has enough available seats
        if (flight.getAvailableSeats() < reservation.getSeats()) {
            throw new InvalidBookingException("Not enough seats available");
        }

        // validate seat type matches
        if (!flight.getSeatType().equalsIgnoreCase(reservation.getSeatType())) {
            throw new InvalidFlightException("Invalid seat type for flight " + reservation.getFlightNumber());
        }

        // replace accents and uppercase chars
        String normalizedOrigin = StringUtil.normalizeString(reservation.getOrigin());
        String normalizedDestination = StringUtil.normalizeString(reservation.getDestination());

        // validate origin matches
        if (!StringUtil.normalizeString(flight.getOrigin()).equals(normalizedOrigin)) {
            throw new InvalidFlightException("Invalid origin for flight " + reservation.getFlightNumber());
        }

        // validate destination matches
        if (!StringUtil.normalizeString(flight.getDestination()).equals(normalizedDestination)) {
            throw new InvalidFlightException("Invalid destination for flight " + reservation.getFlightNumber());
        }

        // parse and validate dates
        LocalDate dateFrom = DateUtil.parseDate(reservation.getDateFrom());
        LocalDate dateTo = DateUtil.parseDate(reservation.getDateTo());

        // validate dates don't overlap
        DateUtil.validateDateRange(dateFrom, dateTo);

        // validate flight is available in requested dates
        if (flight.getDateFrom().compareTo(dateFrom) > 0
                || flight.getDateTo().compareTo(dateTo) < 0) {
            throw new InvalidFlightException("dates not available for flight " + reservation.getFlightNumber());
        }
    }

    private void validatePeopleAmount(Integer seats, Integer peopleListSize) throws InvalidBookingException {

        // validate requested seats match people list size
        if (!seats.equals(peopleListSize)) {
            throw new InvalidBookingException("Seats don't match amount of people");
        }
    }

    private List<FlightDTO> getFilteredFlights(Map<String, String> filters) throws Exception {

        // parse and validate date strings
        LocalDate fromDate = DateUtil.parseDate(filters.get("dateFrom"));
        LocalDate toDate = DateUtil.parseDate(filters.get("dateTo"));

        // validate dates don't overlap
        DateUtil.validateDateRange(fromDate, toDate);

        // replace accents and uppercase chars
        String normalizedOrigin = StringUtil.normalizeString(filters.get("origin"));
        String normalizedDestination = StringUtil.normalizeString(filters.get("destination"));

        // validate origin and destination exist
        validateOrigin(normalizedOrigin);
        validateDestination(normalizedDestination);

        return flightRepository.filterAvailableFlightsByDateAndLocation(fromDate, toDate, normalizedOrigin, normalizedDestination);
    }

    private void validateOrigin(String origin) throws InvalidLocationException {

        // search origin in complete flight list (available or not)
        Optional<FlightDTO> hotelByLocation = flightRepository.getFlightList().stream()
                .filter(flight -> StringUtil.normalizeString(flight.getOrigin()).equals(origin))
                .findAny();

        if (hotelByLocation.isEmpty()) {
            throw new InvalidLocationException("Origin not found");
        }
    }

    private void validateDestination(String destination) throws InvalidLocationException {

        // search destination in complete flight list (available or not)
        Optional<FlightDTO> hotelByLocation = flightRepository.getFlightList().stream()
                .filter(flight -> StringUtil.normalizeString(flight.getDestination()).equals(destination))
                .findAny();

        if (hotelByLocation.isEmpty()) {
            throw new InvalidLocationException("Destination not found");
        }
    }

    private void validateRequiredFilters(Map<String, String> filters) throws InvalidFilterException {

        //validate al four required parameters are present
        if (filters.get("dateFrom") == null
                || filters.get("dateTo") == null
                || filters.get("origin") == null
                || filters.get("destination") == null) {
            throw new InvalidFilterException("Request with filters must include 'dateFrom', 'dateTo', 'origin' and 'destination'");
        }
    }
}
