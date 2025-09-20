package com.project.sunauloNepal.util;

public class PhoneUtil {

    /**
     * Convert Nepali phone numbers into E.164 format (+97798xxxxxxx).
     *
     * @param number raw phone number from user input
     * @return formatted number in +977 format
     */
    public static String formatToE164(String number) {
        if (number == null || number.isBlank()) {
            return null;
        }
        number = number.trim();

        if (number.startsWith("+")) {
            return number; // already in correct format
        }

        if (number.startsWith("98")) { // Nepali mobile numbers
            return "+977" + number;
        }

        throw new IllegalArgumentException("❌ Invalid phone number format: " + number);
    }
}
