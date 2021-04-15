package com.example.quality.utils;


public class StringUtils {

    //ToDo change name?
    public static String normalizeString(String text) {

        return text.toLowerCase().toLowerCase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");
    }
}
