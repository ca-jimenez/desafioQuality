package com.example.quality.services;


import com.example.quality.dtos.HotelDTO;

import java.util.List;
import java.util.Map;

public interface HotelService {

    List<HotelDTO> getAvailableHotels();

    List<HotelDTO> getFilteredAvailableHotels(String dateFrom, String dateTo, String destination) throws Exception;

//    List<ArticleDTO> getUnfilteredArticles();
//
//    List<ArticleDTO> getArticles(Map<String, String> allFilters) throws Exception;
//
//    PurchaseResponseDTO PurchaseArticles(Long cartId, PurchaseRequestDTO articles) throws Exception;
}
