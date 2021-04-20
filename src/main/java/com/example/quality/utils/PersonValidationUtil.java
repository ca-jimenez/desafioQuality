package com.example.quality.utils;

import com.example.quality.dtos.PersonDTO;
import com.example.quality.exceptions.InvalidPersonDataException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonValidationUtil {

    public static void validateEmail(String email) throws InvalidPersonDataException {

        Pattern pattern = Pattern.compile("^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$");
        Matcher matcher = pattern.matcher(email);

        if (!matcher.find()) {
            throw new InvalidPersonDataException("Invalid email: " + email);
        }
    }

    public static void validateDni(String dni) throws InvalidPersonDataException {

        try {
            Long.parseLong(dni);
        } catch (Exception e) {
            throw new InvalidPersonDataException("Invalid DNI");
        }
    }

    public static void validateName(String name) throws InvalidPersonDataException {

        Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])[a-zA-ZñÑáéíóúÁÉÍÓÚü][a-zA-ZñÑáéíóúÁÉÍÓÚü ]{2,20}[a-zA-ZñÑáéíóúÁÉÍÓÚü]$");
        Matcher matcher = pattern.matcher(name);

        if (!matcher.find()) {
            throw new InvalidPersonDataException("Invalid name: " + name);
        }
    }

    public static void validatePeopleData(List<PersonDTO> people) throws Exception {

        for (PersonDTO person : people) {
            validateDni(person.getDni());
            validateName(person.getName());
            validateName(person.getLastname());
            DateUtil.parseDate(person.getBirthDate());
            validateEmail(person.getMail());
        }
    }
}
