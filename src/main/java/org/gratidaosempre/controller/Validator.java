package org.gratidaosempre.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class Validator {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    public static boolean dateTime(String dateTime) {
        if (dateTime == null || !dateTime.matches("[0-9]{2}([/-])[0-9]{2}\\1[0-9]{4}")) {
            return false;
        }
        try {
            return LocalDate.parse(dateTime.replace('-', '/'), DATE_FORMAT).getYear() > 0;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
