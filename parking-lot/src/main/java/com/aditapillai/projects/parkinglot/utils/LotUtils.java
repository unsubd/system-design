package com.aditapillai.projects.parkinglot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LotUtils {
    public static final Pattern registrationNumberPattern = Pattern.compile("^(?<state>[A-Z]{2})(?<rto>\\d{2})" +
            "(?<series>[A-Z]{1,2})" +
            "(?<number>\\d{1,4})$");

    /**
     * Check if the given value is odd
     *
     * @param number to check
     * @return true if the given number is odd else return false
     */
    public static boolean isOdd(int number) {
        return number % 2 != 0;
    }

    /**
     * Get the numeric registration number from the alphanumeric registration number
     *
     * @param registrationNumber The registration number of the car
     * @return The last 4 (1-4) digits in the registration number
     */
    public static int getNumber(String registrationNumber) {
        Matcher matcher = registrationNumberPattern.matcher(registrationNumber);
        int number = -1;
        if (matcher.matches()) {
            number = Integer.parseInt(matcher.group("number"));
        }
        return number;
    }
}
