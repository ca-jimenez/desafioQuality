package com.example.quality.handlers;


public class StringHandler {

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
