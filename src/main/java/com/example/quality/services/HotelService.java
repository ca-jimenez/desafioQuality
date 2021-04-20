package com.example.quality.services;


import com.example.quality.dtos.BookingRequestDTO;
import com.example.quality.dtos.BookingResponseDTO;
import com.example.quality.dtos.HotelDTO;
import com.example.quality.exceptions.InvalidHotelException;

import java.util.List;
import java.util.Map;

public interface HotelService {

//    List<HotelDTO> getAllAvailableHotels();

    List<HotelDTO> getAvailableHotels(Map<String, String> params) throws Exception;

    BookingResponseDTO bookARoom(BookingRequestDTO request) throws Exception;

//    List<ArticleDTO> getUnfilteredArticles();
//
//    List<ArticleDTO> getArticles(Map<String, String> allFilters) throws Exception;
//
//    PurchaseResponseDTO PurchaseArticles(Long cartId, PurchaseRequestDTO articles) throws Exception;
}
