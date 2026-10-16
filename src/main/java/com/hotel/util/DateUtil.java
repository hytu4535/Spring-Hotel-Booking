package com.hotel.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate parse(String dateStr) {
        try {
            return LocalDate.parse(dateStr, FORMATTER);
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    public static String format(LocalDate date) {
        return date != null ? date.format(FORMATTER) : "";
    }

    public static int soDem(LocalDate nhan, LocalDate tra) {
        if (nhan == null || tra == null || !tra.isAfter(nhan)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(nhan, tra);
    }

    public static boolean isValidRange(LocalDate nhan, LocalDate tra) {
        return nhan != null && tra != null && tra.isAfter(nhan);
    }
}
