package com.example.quality.repositories;


import com.example.quality.dtos.HotelDTO;

import java.time.LocalDate;
import java.util.List;

public interface HotelRepository {

    List<HotelDTO> getHotelList();

    List<HotelDTO> getAvailableHotelsList();

    HotelDTO getHotelByCode(String hotelCode);

    List<HotelDTO> filterAvailableHotelsByDateAndDestination(LocalDate fromDate, LocalDate toDate, String destination);

    void reserveHotel(String hotelCode);
}