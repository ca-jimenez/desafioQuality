package com.example.quality.utils;

import com.example.quality.dtos.PersonDTO;
import com.example.quality.exceptions.InvalidPersonDataException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonValidationUtil {

    public static void validateEmail(String email) throws InvalidPersonDataException {

        // pattern accepts generic email format, no uppercase letters
        Pattern pattern = Pattern.compile("^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$");
        Matcher matcher = pattern.matcher(email);

        if (!matcher.find()) {
            throw new InvalidPersonDataException("Invalid email: " + email);
        }
    }

    public static void validateDni(String dni) throws InvalidPersonDataException {

        // validate dni is numeric
        try {
            Long.parseLong(dni);
        } catch (Exception e) {
            throw new InvalidPersonDataException("Invalid DNI");
        }
    }

    public static void validateName(String name) throws InvalidPersonDataException {

        // pattern accepts upper and lowercase letters, ñ char and accented chars, length 4 to 22 chars, at least one letter
        Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])[a-zA-ZñÑáéíóúÁÉÍÓÚü][a-zA-ZñÑáéíóúÁÉÍÓÚü ]{2,20}[a-zA-ZñÑáéíóúÁÉÍÓÚü]$");
        Matcher matcher = pattern.matcher(name);

        if (!matcher.find()) {
            throw new InvalidPersonDataException("Invalid name: " + name);
        }
    }

    public static void validatePeopleData(List<PersonDTO> people) throws Exception {

        // validates contents of list of people sent in request
        for (PersonDTO person : people) {
            validateDni(person.getDni());
            validateName(person.getName());
            validateName(person.getLastname());
            DateUtil.parseDate(person.getBirthDate());
            validateEmail(person.getMail());
        }
    }
}
