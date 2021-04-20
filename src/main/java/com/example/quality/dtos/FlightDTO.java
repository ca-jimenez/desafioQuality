package com.example.quality.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightDTO {

    private Long flightId;
    private String flightNumber;
    private String origin;
    private String destination;
    private String seatType;
    private Integer pricePerPerson;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Integer availableSeats;
}
