package com.example.quality.repositories;

import com.example.quality.dtos.HotelDTO;
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
public class HotelRepositoryImpl implements HotelRepository {

    private final AtomicLong idCounter = new AtomicLong(1);

    private List<HotelDTO> hotelList;

    public HotelRepositoryImpl(@Value("${hotels_path:hotels.csv}") String path) {
        hotelList = loadDatabase(path);
    }

    @Override
    public List<HotelDTO> getHotelList() {
        return hotelList;
    }

    @Override
    public List<HotelDTO> getAvailableHotelsList() {
        return hotelList.stream()
                .filter(hotel -> hotel.getReserved().equals(false))
                .collect(Collectors.toList());
    }

    @Override
    public HotelDTO getHotelByCode(String hotelCode) {
        return hotelList.stream()
                .filter(hotel -> hotel.getCode().equalsIgnoreCase(hotelCode))
                .findFirst().orElse(null);
    }

    @Override
    public List<HotelDTO> filterAvailableHotelsByDateAndDestination(LocalDate fromDate, LocalDate toDate, String destination) {
        return getAvailableHotelsList().stream()
                .filter(hotel -> hotel.getAvailableFrom().compareTo(fromDate) <= 0
                        && hotel.getAvailableTo().compareTo(toDate) >= 0)
                .filter(hotel -> StringUtil.normalizeString(hotel.getCity())
                        .equals(destination))
                .collect(Collectors.toList());
    }

    @Override
    public void reserveHotel(String hotelCode) {

        getHotelByCode(hotelCode).setReserved(true);

        updateDatabase();
    }

    //Overwrite csv file with updated data
    private void updateDatabase() {

        String recordAsCsv = hotelList.stream()
                .map(StringUtil::hotelToCsvRow)
                .collect(Collectors.joining(System.getProperty("line.separator")));

        try {
            // File path
            FileWriter writer = new FileWriter("src/main/resources/hotels.csv");

            // Add headers
            writer.append("code,name,city,roomType,pricePerNight,availableFrom,availableTo,reserved\n");

            // Add content
            writer.append(recordAsCsv);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Parse csv file data
    private List<HotelDTO> loadDatabase(String path) {

        List<HotelDTO> records = new ArrayList<>();

        try {

            Resource resource = new ClassPathResource(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            String row;

            // Skip headers
            reader.readLine();

            while ((row = reader.readLine()) != null) {
                String[] data = row.split(",");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                String code = data[0];
                String name = data[1];
                String city = data[2];
                String roomType = data[3];

                Integer price = Integer.parseInt(data[4]
                        .replace("$", ""));

                LocalDate fromDate = LocalDate.parse(data[5], formatter);
                LocalDate toDate = LocalDate.parse(data[6], formatter);

                Boolean reserved = data[7].equals("SI");

                records.add(new HotelDTO(idCounter.getAndIncrement(), code, name, city, roomType, price, fromDate, toDate, reserved));
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return records;
    }
}
