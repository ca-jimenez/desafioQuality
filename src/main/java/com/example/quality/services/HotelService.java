package com.example.quality.services;


import com.example.quality.dtos.BookingRequestDTO;
import com.example.quality.dtos.BookingResponseDTO;
import com.example.quality.dtos.HotelDTO;

import java.util.List;
import java.util.Map;

public interface HotelService {

    List<HotelDTO> getAvailableHotels(Map<String, String> params) throws Exception;

    BookingResponseDTO bookARoom(BookingRequestDTO request) throws Exception;
}
