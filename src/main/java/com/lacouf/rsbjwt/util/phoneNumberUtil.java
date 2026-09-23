package com.lacouf.rsbjwt.util;

import com.lacouf.rsbjwt.Exception.ChampsObligatoiresManquants;
import com.lacouf.rsbjwt.Exception.FormatTelephoneNonValide;

public class phoneNumberUtil {
    public static void phoneNumberValidator(String phoneNumber) throws ChampsObligatoiresManquants, FormatTelephoneNonValide {
        if (phoneNumber == null) {
            throw new ChampsObligatoiresManquants();
        }
        if (!phoneNumber.trim().matches("[0-9]{3}-?[0-9]{3}-?[0-9]{4}")) {
            throw new FormatTelephoneNonValide();
        }
    }

    public static String formatePhoneNumber(String phoneNumber) throws ChampsObligatoiresManquants, FormatTelephoneNonValide {
        phoneNumberValidator(phoneNumber);

        String digitsOnly = phoneNumber.replace("-", "");

        return String.format("%s-%s-%s",
                digitsOnly.substring(0, 3),
                digitsOnly.substring(3, 6),
                digitsOnly.substring(6, 10));
    }
}
