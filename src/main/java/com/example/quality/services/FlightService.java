package com.example.quality.services;


import com.example.quality.dtos.*;

import java.util.List;
import java.util.Map;

public interface FlightService {

//    List<HotelDTO> getAllAvailableHotels();

    List<FlightDTO> getAvailableFlights(Map<String, String> params) throws Exception;

    FlightReservationResponseDTO bookAFlight(FlightReservationRequestDTO request) throws Exception;

//    List<ArticleDTO> getUnfilteredArticles();
//
//    List<ArticleDTO> getArticles(Map<String, String> allFilters) throws Exception;
//
//    PurchaseResponseDTO PurchaseArticles(Long cartId, PurchaseRequestDTO articles) throws Exception;
}
