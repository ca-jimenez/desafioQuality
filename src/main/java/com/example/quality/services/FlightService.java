package com.example.quality.services;


import com.example.quality.dtos.*;

import java.util.List;
import java.util.Map;

public interface FlightService {

    List<FlightDTO> getAvailableFlights(Map<String, String> params) throws Exception;

    FlightReservationResponseDTO bookAFlight(FlightReservationRequestDTO request) throws Exception;
}
