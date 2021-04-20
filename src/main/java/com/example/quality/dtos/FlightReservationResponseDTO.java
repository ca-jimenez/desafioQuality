package com.example.quality.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightReservationResponseDTO {

    private String username;
    private Double amount;
    private Integer interest;
    private Double total;
    private FlightReservationDTO flightReservation;
    private StatusDTO statusCode;
}
