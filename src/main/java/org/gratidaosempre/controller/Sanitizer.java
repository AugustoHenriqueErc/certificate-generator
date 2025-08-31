package org.gratidaosempre.controller;

public class Sanitizer {
    //Sanitizes string for Directory as a Local-date String
    public static String replaceBar(String date) {
        return date.trim().replace("/", "_");
    }
    //Sanitizes string for Directory as a Local-date String
    public static String replaceSpace(String local) {
        return local.trim().replace(" ", "_");
    }

}
