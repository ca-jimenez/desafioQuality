package com.example.quality.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {

    private Long hotelId;
    private String code;
    private String name;
    private String city;
    private String roomType;
    private Integer pricePerNight;
    private LocalDate availableFrom;
    private LocalDate availableTo;
    private Boolean reserved;


//    public void subtractQuantity(Integer quantity) {
//        this.quantity -= quantity;
//    }
//
//    private String formatPrice() {
//        return "$" + String.format("%,d", price).replace(",", ".");
//    }
//
//    private String formatShipping() {
//        return freeShipping? "SI" : "NO";
//    }
//
//    private String formatPrestige() {
//
//        String result = "";
//        for (int i = 0; i < prestige; i++) {
//            result += "*";
//        }
//        return result;
//    }
//
//    // Parse ArticleDTO to csv formatted string
//    public String toCsvRow() {
//        return String.join(",", name, category, brand, formatPrice(), quantity.toString(), formatShipping(), formatPrestige());
//    }
//
//    //---------------------
//    @Override
//    public String toString() {
//        return "{\"productId\":" + productId + ", \"name\":\"" + name + "\", \"category\":\"" + category + "\", \"brand\":\"" + brand + "\", \"price\":" + price + ", \"quantity\":" + quantity + ", \"freeShipping\":" + freeShipping + ", \"prestige\":" + prestige + "}";
//    }
}
