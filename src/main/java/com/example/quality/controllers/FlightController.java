package com.example.quality.controllers;


import com.example.quality.dtos.*;
import com.example.quality.services.FlightService;
import com.example.quality.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }


    @GetMapping("flights")
    public ResponseEntity<List<FlightDTO>> getFlights(@RequestParam Map<String, String> params) throws Exception {

        return new ResponseEntity<>(flightService.getAvailableFlights(params), HttpStatus.OK);
    }

    @PostMapping("/flight-reservation")
    public ResponseEntity<FlightReservationResponseDTO> bookFlight(@RequestBody FlightReservationRequestDTO request) throws Exception {

        return new ResponseEntity<>(flightService.bookAFlight(request), HttpStatus.OK);
    }
}

//ToDo readme + docs + tests + comments
// validate json format
