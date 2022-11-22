package com.internetbanking.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String BIRTHDAY_FORMAT = "dd-MM-yyyy";

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    public static String toString(LocalDateTime date) {
        return date.format(formatter);
    }

    public static LocalDateTime toDate(String date) {
        return LocalDateTime.parse(date, formatter);
    }
}
