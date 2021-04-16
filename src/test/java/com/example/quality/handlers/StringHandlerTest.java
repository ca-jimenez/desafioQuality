package com.example.quality.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringHandlerTest {

//    @BeforeEach
//    void setUp() {
//    }

    @Test
    @DisplayName("Should Return Normalized String")
    void normalizeString() {
        assertEquals(StringHandler.normalizeString("Puérto Iguazú"), "puerto iguazu");
    }

    @Test
    @DisplayName("Should Return Same String")
    void normalizeStringNoAccents() {
        assertEquals(StringHandler.normalizeString("puerto iguazu"), "puerto iguazu");
    }
}