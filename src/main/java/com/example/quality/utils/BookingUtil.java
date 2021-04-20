package com.example.quality.utils;

import com.example.quality.dtos.PaymentMethodDTO;
import com.example.quality.exceptions.InvalidPaymentException;

public class BookingUtil {

    public static Integer getInterestPercentage(PaymentMethodDTO paymentMethod) throws InvalidPaymentException {

        String type = paymentMethod.getType().toLowerCase();
        Integer dues = paymentMethod.getDues();

        int interest = 0;

        switch (type) {

            case "debit":
                //validate debit dues has to be 1
                if (dues != 1) {
                    throw new InvalidPaymentException("Accepted payment dues value for debit is 1");
                }
                break;

            case "credit":
                // assign interest rate according to number of dues
                if (dues == 1) {
                    interest = 0;
                } else if (dues > 1 && dues <= 3) {
                    interest = 5;
                } else if (dues > 3 && dues <= 6) {
                    interest = 10;
                } else {
                    throw new InvalidPaymentException("Accepted payment dues values for credit are 1-6");
                }
                break;

            default:
                throw new InvalidPaymentException("Accepted payment method types are credit and debit");
        }

        return interest;
    }

    public static Double calculateTotalWithInterests(Double total, Integer interest) {

        return total + (total * interest / 100);
    }
}
