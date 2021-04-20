package com.example.quality.controllers;


import com.example.quality.dtos.BookingRequestDTO;
import com.example.quality.dtos.BookingResponseDTO;
import com.example.quality.dtos.HotelDTO;
import com.example.quality.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/api/v1")
public class HotelController {

    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("hotels")
    public ResponseEntity<List<HotelDTO>> getHotels(@RequestParam Map<String, String> params) throws Exception {

        return new ResponseEntity<>(hotelService.getAvailableHotels(params), HttpStatus.OK);
    }

    @PostMapping("/booking")
    public ResponseEntity<BookingResponseDTO> bookHotelRoom(@RequestBody BookingRequestDTO request) throws Exception {

        return new ResponseEntity<>(hotelService.bookARoom(request), HttpStatus.OK);
    }
}

//ToDo readme + docs + tests + comments
// validate json format
