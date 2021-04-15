package com.example.quality.repositories;


import com.example.quality.dtos.HotelDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class HotelRepositoryImpl implements HotelRepository {

//    private String path;

    private final AtomicLong idCounter = new AtomicLong(1);

    private List<HotelDTO> hotelList;

    @Override
    public List<HotelDTO> getHotelList() {
        return hotelList;
    }


    public HotelRepositoryImpl(@Value("${hotels_path:hotels.csv}") String path) {
        hotelList = loadDatabase(path);
    }

    //
//    @Override
//    public List<ArticleDTO> getArticleList() {
//        return catalog;
//    }
//
//    @Override
//    public ArticleDTO getArticleById(Long id) {
//        return catalog.stream()
//                .filter(a -> a.getProductId().equals(id))
//                .findFirst()
//                .orElse(null);
//    }
//
//    @Override
//    public void subtractStock(Long id, Integer quantity) {
//        getArticleById(id).subtractQuantity(quantity);
//    }
//
//    //Overwrite csv file with updated catalog data
//    @Override
//    public void updateDatabase() {
//
//        String recordAsCsv = catalog.stream()
//                .map(ArticleDTO::toCsvRow)
//                .collect(Collectors.joining(System.getProperty("line.separator")));
//
//        try {
//            // File path
//            FileWriter writer = new FileWriter("src/main/resources/dbProductos.csv");
//
//            // Add headers
//            writer.append("name,category,brand,price,quantity,freeShipping,prestige\n");
//
//            // Add content
//            writer.append(recordAsCsv);
//            writer.flush();
//            writer.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
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
