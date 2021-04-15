package com.example.quality.controllers;


import com.example.quality.dtos.HotelDTO;
import com.example.quality.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HotelController {

    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping(value = "/hotels", params = {"!dateFrom", "!dateTo", "!destination"})
    public ResponseEntity<List<HotelDTO>> getAllAvailableHotels() {

        return new ResponseEntity<>(hotelService.getAvailableHotels(), HttpStatus.OK);
    }


    //ToDo check variations
    @GetMapping(value = "/hotels", params = {"dateFrom", "dateTo", "destination"})
    public ResponseEntity<List<HotelDTO>> getAvailableHotelsByDateAndDestination(
            @RequestParam String dateFrom,
            @RequestParam String dateTo,
            @RequestParam String destination) throws Exception {

        return new ResponseEntity<>(hotelService.getFilteredAvailableHotels(dateFrom, dateTo, destination), HttpStatus.OK);
    }
//
//    @PostMapping("/purchase-request")
//    public ResponseEntity<PurchaseResponseDTO> makePurchase(
//            @RequestParam(value = "shoppingCartId", required = false) Long shoppingCartId,
//            @RequestBody PurchaseRequestDTO articles) throws Exception {
//
//        return new ResponseEntity<>(marketService.PurchaseArticles(shoppingCartId, articles), HttpStatus.OK);
//    }
}

//ToDo readme + docs + tests + comments
