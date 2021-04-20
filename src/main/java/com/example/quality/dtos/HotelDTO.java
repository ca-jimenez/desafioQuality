package com.example.quality.dtos;

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
}
