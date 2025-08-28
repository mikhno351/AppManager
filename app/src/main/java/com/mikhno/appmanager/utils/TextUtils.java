package com.mikhno.appmanager.utils;

import java.util.Arrays;
import java.util.List;

public class TextUtils {

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static String multipartDeveloper(String... developers) {
        if (developers == null || developers.length == 0) {
            return "";
        }

        List<String> developerList = Arrays.asList(developers);

        if (developerList.size() == 1) {
            return developerList.get(0);
        } else if (developerList.size() == 2) {
            return developerList.get(0) + " & " + developerList.get(1);
        } else {
            String allButLast = String.join(", ", developerList.subList(0, developerList.size() - 1));
            String last = developerList.get(developerList.size() - 1);
            return allButLast + " & " + last;
        }
    }
}
