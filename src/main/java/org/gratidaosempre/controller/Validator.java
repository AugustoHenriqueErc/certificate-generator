package org.gratidaosempre.controller;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Validator {

    public static boolean dateTime(String dateTime) {
        String regex = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-\\d{4}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dateTime);

        if(!matcher.find()) {return false;}
        return true;
    }
}
