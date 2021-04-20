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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    //ToDo contemplate repository empty list

    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }


    @Override
    public List<FlightDTO> getAvailableFlights(Map<String, String> params) throws Exception {

        List<FlightDTO> allAvailableHotels = flightRepository.getAvailableFlightsList();

        if (params.size() < 1) {

            return allAvailableHotels;

        } else if (params.size() == 4) {

            validateRequiredFilters(params);
            return getFilteredFlights(params);

        } else {
            throw new InvalidFilterException("Accepted filters are 'dateFrom', 'dateTo', 'origin' and 'destination'");
        }
    }

    @Override
    public FlightReservationResponseDTO bookAFlight(FlightReservationRequestDTO request) throws Exception {

        PersonValidationUtil.validateEmail(request.getUsername());

        FlightReservationDTO reservation = request.getFlightReservation();
        FlightDTO flight = flightRepository.getFlightByCode(reservation.getFlightNumber());

        validateFlight(flight, reservation);
        validatePeopleAmount(reservation.getSeats(), reservation.getPeople().size());
        PersonValidationUtil.validatePeopleData(reservation.getPeople());
        Double amount = calculateFlightBaseAmount(flight, reservation);
        Integer interest = BookingUtil.getInterestPercentage(reservation.getPaymentMethod());
        Double amountWithInterests = BookingUtil.calculateTotalWithInterests(amount, interest);

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

//        FlightDTO flight = flightRepository.getFlightByCode(reservation.getFlightNumber());

        return flight.getPricePerPerson() * (double) reservation.getSeats();
    }


    private void validateFlight(FlightDTO flight, FlightReservationDTO reservation) throws Exception {

//        FlightDTO flight = flightRepository.getFlightByCode(reservation.getFlightNumber());

        if (flight == null) {
            throw new InvalidFlightException("Flight with code " + reservation.getFlightNumber() + " not found");
        }

        if (flight.getAvailableSeats() < reservation.getSeats()) {
            throw new InvalidBookingException("Not enough seats available");
        }

        if (!flight.getSeatType().equalsIgnoreCase(reservation.getSeatType())) {
            throw new InvalidFlightException("Invalid seat type for flight " + reservation.getFlightNumber());
        }

        String normalizedOrigin = StringUtil.normalizeString(reservation.getOrigin());
        String normalizedDestination = StringUtil.normalizeString(reservation.getDestination());

        if (!StringUtil.normalizeString(flight.getOrigin()).equals(normalizedOrigin)) {
            throw new InvalidFlightException("Invalid origin for flight " + reservation.getFlightNumber());
        }

        if (!StringUtil.normalizeString(flight.getDestination()).equals(normalizedDestination)) {
            throw new InvalidFlightException("Invalid destination for flight " + reservation.getFlightNumber());
        }

        LocalDate dateFrom = DateUtil.parseDate(reservation.getDateFrom());
        LocalDate dateTo = DateUtil.parseDate(reservation.getDateTo());

        DateUtil.validateDateRange(dateFrom, dateTo);

        //todo dates must match?
        if (flight.getDateFrom().compareTo(dateFrom) > 0
                || flight.getDateTo().compareTo(dateTo) < 0) {
            throw new InvalidFlightException("dates not available for flight " + reservation.getFlightNumber());
        }
    }

    private void validatePeopleAmount(Integer seats, Integer peopleListSize) throws InvalidBookingException {

        if (!seats.equals(peopleListSize)) {
            throw new InvalidBookingException("Seats don't match amount of people");
        }
    }

    private List<FlightDTO> getFilteredFlights(Map<String, String> filters) throws Exception {

        LocalDate fromDate = DateUtil.parseDate(filters.get("dateFrom"));
        LocalDate toDate = DateUtil.parseDate(filters.get("dateTo"));

        DateUtil.validateDateRange(fromDate, toDate);

        String normalizedOrigin = StringUtil.normalizeString(filters.get("origin"));
        String normalizedDestination = StringUtil.normalizeString(filters.get("destination"));

        validateOrigin(normalizedOrigin);
        validateDestination(normalizedDestination);

        return flightRepository.filterAvailableFlightsByDateAndLocation(fromDate, toDate, normalizedOrigin, normalizedDestination);
    }

    private void validateOrigin(String origin) throws InvalidLocationException {

        Optional<FlightDTO> hotelByLocation = flightRepository.getFlightList().stream()
                .filter(flight -> StringUtil.normalizeString(flight.getOrigin()).equals(origin))
                .findAny();

        if (hotelByLocation.isEmpty()) {
            throw new InvalidLocationException("Origin not found");
        }
    }

    private void validateDestination(String destination) throws InvalidLocationException {

        Optional<FlightDTO> hotelByLocation = flightRepository.getFlightList().stream()
                .filter(flight -> StringUtil.normalizeString(flight.getDestination()).equals(destination))
                .findAny();

        if (hotelByLocation.isEmpty()) {
            throw new InvalidLocationException("Destination not found");
        }
    }

    private void validateRequiredFilters(Map<String, String> filters) throws InvalidFilterException {

        if (filters.get("dateFrom") == null
                || filters.get("dateTo") == null
                || filters.get("origin") == null
                || filters.get("destination") == null) {
            throw new InvalidFilterException("Request with filters must include 'dateFrom', 'dateTo', 'origin' and 'destination'");
        }
    }
}
