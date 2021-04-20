package com.example.quality.utils;

import com.example.quality.exceptions.InvalidPersonDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonValidationUtilTest {

    @Test
    @DisplayName("Should not throw invalid email exception")
    void validateEmail() {

        String email1 = "mymail_23@email.com";
        String email2 = "mymail@email.com.ar";
        String email3 = "my.mail_23@email.com.co";
        String email4 = "mymail_23@email.gov.ar";

        assertDoesNotThrow(() -> PersonValidationUtil.validateEmail(email1));
        assertDoesNotThrow(() -> PersonValidationUtil.validateEmail(email2));
        assertDoesNotThrow(() -> PersonValidationUtil.validateEmail(email3));
        assertDoesNotThrow(() -> PersonValidationUtil.validateEmail(email4));
    }

    @Test
    @DisplayName("Should throw invalid email exception")
    void validateInvalidEmail() {

        String email1 = "mymail_23email.com";
        String email2 = "mymail@email";
        String email3 = "mymai+l_23@email.com.co";
        String email4 = "@email.gov.ar";

        assertThrows(InvalidPersonDataException.class,
                () -> PersonValidationUtil.validateEmail(email1));
        assertThrows(InvalidPersonDataException.class,
                () -> PersonValidationUtil.validateEmail(email2));
        assertThrows(InvalidPersonDataException.class,
                () -> PersonValidationUtil.validateEmail(email3));
        assertThrows(InvalidPersonDataException.class,
                () -> PersonValidationUtil.validateEmail(email4));
    }

    @Test
    @DisplayName("Should not throw invalid dni exception")
    void validateDni() {

        assertDoesNotThrow(() -> PersonValidationUtil.validateDni("123456786"));
    }

    @Test
    @DisplayName("Should throw invalid dni exception")
    void validateInvalidDni() {

        String dni1 = "1314 345";
        String dni2 = "131.3435";
        String dni3 = "dni";

        assertThrows(InvalidPersonDataException.class,
                () -> PersonValidationUtil.validateDni(dni1));
        assertThrows(InvalidPersonDataException.class,
                () -> PersonValidationUtil.validateDni(dni2));
        assertThrows(InvalidPersonDataException.class,
                () -> PersonValidationUtil.validateDni(dni3));
    }

    @Test
    @DisplayName("Should not throw invalid name exception")
    void validateName() {

        String name1 = "Carlos";
        String name2 = "Pérez";
        String name3 = "María Juana";
        String name4 = "juan";

        assertDoesNotThrow(() -> PersonValidationUtil.validateName(name1));
        assertDoesNotThrow(() -> PersonValidationUtil.validateName(name2));
        assertDoesNotThrow(() -> PersonValidationUtil.validateName(name3));
        assertDoesNotThrow(() -> PersonValidationUtil.validateName(name4));
    }

    @Test
    @DisplayName("Should throw invalid name exception")
    void validateInvalidName() {

        String name1 = " Carlos";
        String name2 = "Pé{rez";
        String name3 = "Màría Juana";
        String name4 = "     ";
        String name5 = "ju";

        assertThrows(InvalidPersonDataException.class,
                () -> PersonValidationUtil.validateName(name1));
        assertThrows(InvalidPersonDataException.class,
                () -> PersonValidationUtil.validateName(name2));
        assertThrows(InvalidPersonDataException.class,
                () -> PersonValidationUtil.validateName(name3));
        assertThrows(InvalidPersonDataException.class,
                () -> PersonValidationUtil.validateName(name4));
        assertThrows(InvalidPersonDataException.class,
                () -> PersonValidationUtil.validateName(name5));
    }
}