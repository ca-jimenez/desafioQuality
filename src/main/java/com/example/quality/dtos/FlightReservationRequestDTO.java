package com.example.quality.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightReservationRequestDTO {

    private String username;
    private FlightReservationDTO flightReservation;
}
