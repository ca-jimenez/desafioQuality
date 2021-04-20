package com.example.quality.utils;

import com.example.quality.dtos.PaymentMethodDTO;
import com.example.quality.exceptions.InvalidPaymentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingUtilTest {

    @Test
    @DisplayName("Should return 0% interest")
    void getInterestPercentageCreditOneDue() throws InvalidPaymentException {

        PaymentMethodDTO paymentMethod1 = new PaymentMethodDTO();
        paymentMethod1.setDues(1);
        paymentMethod1.setType("credit");

        PaymentMethodDTO paymentMethod2 = new PaymentMethodDTO();
        paymentMethod2.setDues(1);
        paymentMethod2.setType("debit");

        assertEquals(0, BookingUtil.getInterestPercentage(paymentMethod1));
        assertEquals(0, BookingUtil.getInterestPercentage(paymentMethod2));
    }

    @Test
    @DisplayName("Should return 5% interest")
    void getInterestPercentageThreeDues() throws InvalidPaymentException {

        PaymentMethodDTO paymentMethod = new PaymentMethodDTO();
        paymentMethod.setDues(3);
        paymentMethod.setType("credit");

        assertEquals(5, BookingUtil.getInterestPercentage(paymentMethod));
    }

    @Test
    @DisplayName("Should return 10% interest")
    void getInterestPercentageSixDues() throws InvalidPaymentException {

        PaymentMethodDTO paymentMethod = new PaymentMethodDTO();
        paymentMethod.setDues(6);
        paymentMethod.setType("credit");

        assertEquals(10, BookingUtil.getInterestPercentage(paymentMethod));
    }

    @Test
    @DisplayName("Should throw invalid payment type")
    void getInterestPercentageInvalidType() throws InvalidPaymentException {

        PaymentMethodDTO paymentMethod = new PaymentMethodDTO();
        paymentMethod.setDues(1);
        paymentMethod.setType("cash");

        InvalidPaymentException exception = assertThrows(InvalidPaymentException.class,
                () -> BookingUtil.getInterestPercentage(paymentMethod),
                "Invalid Payment exception expected but not thrown");

        assertEquals("Accepted payment method types are credit and debit",
                exception.getMessage());
    }

    @Test
    @DisplayName("Should throw invalid dues for debit")
    void getInterestPercentageDebitDues() throws InvalidPaymentException {

        PaymentMethodDTO paymentMethod = new PaymentMethodDTO();
        paymentMethod.setDues(2);
        paymentMethod.setType("debit");

        InvalidPaymentException exception = assertThrows(InvalidPaymentException.class,
                () -> BookingUtil.getInterestPercentage(paymentMethod),
                "Invalid Payment exception expected but not thrown");

        assertEquals("Accepted payment dues value for debit is 1",
                exception.getMessage());
    }

    @Test
    @DisplayName("Should throw invalid dues credit")
    void getInterestPercentage() throws InvalidPaymentException {

        PaymentMethodDTO paymentMethod = new PaymentMethodDTO();
        paymentMethod.setDues(10);
        paymentMethod.setType("credit");

        InvalidPaymentException exception = assertThrows(InvalidPaymentException.class,
                () -> BookingUtil.getInterestPercentage(paymentMethod),
                "Invalid Payment exception expected but not thrown");

        assertEquals("Accepted payment dues values for credit are 1-6",
                exception.getMessage());

    }

    @Test
    @DisplayName("Should return total with interests")
    void calculateTotalWithInterests() {
        Double amount = 10085.0;
        Integer interest = 5;

        Double total = 10589.25;
        assertEquals(total, BookingUtil.calculateTotalWithInterests(amount, interest));
    }
}