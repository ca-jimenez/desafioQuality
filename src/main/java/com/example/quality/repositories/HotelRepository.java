package com.example.quality.repositories;


import com.example.quality.dtos.HotelDTO;

import java.util.List;

public interface HotelRepository {

    List<HotelDTO> getHotelList();

//    List<ArticleDTO> getArticleList();
//
//    ArticleDTO getArticleById(Long id);
//
//    void subtractStock(Long id, Integer quantity);
//
//    void updateDatabase();
}