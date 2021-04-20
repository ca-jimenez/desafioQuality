package com.example.quality.utils;


import com.example.quality.dtos.FlightDTO;
import com.example.quality.dtos.HotelDTO;
import com.example.quality.exceptions.InvalidPersonDataException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static String normalizeString(String text) {

        return text.toLowerCase().toLowerCase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");
    }

    public static String formatPrice(Integer price) {
        return "$" + price;
    }


    public static String hotelToCsvRow(HotelDTO hotel) {

        return String.join(",",
                hotel.getCode(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getRoomType(),
                formatPrice(hotel.getPricePerNight()),
                DateUtil.formatDateToString(hotel.getAvailableFrom()),
                DateUtil.formatDateToString(hotel.getAvailableTo()),
                hotel.getReserved() ? "SI" : "NO"
        );
    }

    public static String flightToCsvRow(FlightDTO flight) {

        return String.join(",",
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getSeatType(),
                formatPrice(flight.getPricePerPerson()),
                DateUtil.formatDateToString(flight.getDateFrom()),
                DateUtil.formatDateToString(flight.getDateTo()),
                flight.getAvailableSeats().toString()
        );
    }
}
