package com.example.quality.repositories;


import com.example.quality.dtos.FlightDTO;
import com.example.quality.dtos.HotelDTO;

import java.time.LocalDate;
import java.util.List;

public interface FlightRepository {

    List<FlightDTO> getFlightList();

    List<FlightDTO> getAvailableFlightsList();

    FlightDTO getFlightByCode(String flightCode);

    List<FlightDTO> filterAvailableFlightsByDateAndLocation(LocalDate fromDate, LocalDate toDate, String origin, String destination);

    void reserveFlight(String flightNumber, Integer seats);

//    List<ArticleDTO> getArticleList();
//
//    ArticleDTO getArticleById(Long id);
//
//    void subtractStock(Long id, Integer quantity);
//
//    void updateDatabase();
}