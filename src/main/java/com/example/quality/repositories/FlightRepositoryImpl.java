package com.example.quality.repositories;


import com.example.quality.dtos.FlightDTO;
import com.example.quality.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class FlightRepositoryImpl implements FlightRepository {

    private final AtomicLong idCounter = new AtomicLong(1);

    private List<FlightDTO> flightList;

    public FlightRepositoryImpl(@Value("${flights_path:flights.csv}") String path) {

        flightList = loadDatabase(path);
    }

    @Override
    public List<FlightDTO> getFlightList() {
        return flightList;
    }

    @Override
    public List<FlightDTO> getAvailableFlightsList() {

        // return flights with availableSeats greater than 0
        return flightList.stream()
                .filter(flight -> flight.getAvailableSeats() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public FlightDTO getFlightByCode(String flightNumber) {

        //search flight by flightNumber
        return flightList.stream()
                .filter(flight -> flight.getFlightNumber().equalsIgnoreCase(flightNumber))
                .findFirst().orElse(null);
    }

    @Override
    public List<FlightDTO> filterAvailableFlightsByDateAndLocation(LocalDate fromDate, LocalDate toDate, String origin, String destination) {

        // return filtered flights by date, origin and destination with availableSeats greater than 0
        return getAvailableFlightsList().stream()
                .filter(flight -> flight.getDateFrom().compareTo(fromDate) <= 0
                        && flight.getDateTo().compareTo(toDate) >= 0)
                .filter(flight -> StringUtil.normalizeString(flight.getOrigin()).equals(origin)
                        && StringUtil.normalizeString(flight.getDestination()).equals(destination))
                .collect(Collectors.toList());
    }

    @Override
    public void reserveFlight(String flightNumber, Integer seats) {

        // get available seats for flight
        Integer availableSeats = getFlightByCode(flightNumber).getAvailableSeats();

        // update flight availableSeats in memory
        getFlightByCode(flightNumber).setAvailableSeats(availableSeats - seats);

        //replace csv file contents
        updateDatabase();
    }

    //Overwrite csv file with updated data
    private void updateDatabase() {

        // map flight list contents to cvs strings
        String recordAsCsv = flightList.stream()
                .map(StringUtil::flightToCsvRow)
                .collect(Collectors.joining(System.getProperty("line.separator")));

        try {
            // File path
            FileWriter writer = new FileWriter("src/main/resources/flights.csv");

            // Add headers
            writer.append("flightNumber,origin,destination,seatType,pricePerPerson,dateFrom,dateTo,availableSeats\n");

            // Add content
            writer.append(recordAsCsv);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Parse csv file data
    private List<FlightDTO> loadDatabase(String path) {

        List<FlightDTO> records = new ArrayList<>();

        try {

            Resource resource = new ClassPathResource(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            String row;

            // Skip headers
            reader.readLine();

            while ((row = reader.readLine()) != null) {

                //get data from line
                String[] data = row.split(",");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                // assign data to new object
                String flightNumber = data[0];
                String origin = data[1];
                String destination = data[2];
                String seatType = data[3];

                Integer price = Integer.parseInt(data[4]
                        .replace("$", ""));

                LocalDate fromDate = LocalDate.parse(data[5], formatter);
                LocalDate toDate = LocalDate.parse(data[6], formatter);

                Integer availableSeats = Integer.parseInt(data[7]);

                records.add(new FlightDTO(idCounter.getAndIncrement(), flightNumber, origin, destination, seatType, price, fromDate, toDate, availableSeats));
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return records;
    }
}
