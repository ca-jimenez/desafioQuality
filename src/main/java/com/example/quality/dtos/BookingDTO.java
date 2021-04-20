package com.example.quality.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {

    private String dateFrom;
    private String dateTo;
    private String destination;
    private String hotelCode;
    private Integer peopleAmount;
    private String roomType;
    private List<PersonDTO> people;
    private PaymentMethodDTO paymentMethod;
}
