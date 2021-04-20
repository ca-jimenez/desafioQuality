package com.example.quality.utils;


import com.example.quality.dtos.FlightDTO;
import com.example.quality.dtos.HotelDTO;

public class StringUtil {

    public static String normalizeString(String text) {

        //replace accents and uppercase characters
        return text.toLowerCase().toLowerCase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");
    }

    public static String formatPrice(Integer price) {

        //add $ sign required for csv file
        return "$" + price;
    }

    public static String hotelToCsvRow(HotelDTO hotel) {

        // parse HotelDTO to CSV line string
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

        // parse FlightDTO to CSV line string
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
