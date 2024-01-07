package com.mixno35.appmanager.utils;

import androidx.annotation.NonNull;

public class AndroidUtils {

    /**
     * @param sdk Version Android SDK
     * @param format Format Value | %a - Text "Android", %c - Internal Code Name, %s - SDK, %v - Android Version | "%a %v %c (%s)"
     */
    public static String getName(int sdk, @NonNull String format) {
        String[] android;

        if (sdk == 1) android = new String[]{"1.0", "-"};
        else if (sdk == 2) android = new String[]{"1.1", "Petit Four"};
        else if (sdk == 3) android = new String[]{"1.5", "Cupcake"};
        else if (sdk == 4) android = new String[]{"1.6", "Donut"};
        else if (sdk == 5) android = new String[]{"2.0", "Eclair"};
        else if (sdk == 6) android = new String[]{"2.0", "Eclair"};
        else if (sdk == 7) android = new String[]{"2.1", "Eclair"};
        else if (sdk == 8) android = new String[]{"2.2", "Froyo"};
        else if (sdk == 9) android = new String[]{"2.3", "Gingerbread"};
        else if (sdk == 10) android = new String[]{"2.3", "Gingerbread"};
        else if (sdk == 11) android = new String[]{"3.0", "Honeycomb"};
        else if (sdk == 12) android = new String[]{"3.1", "Honeycomb"};
        else if (sdk == 13) android = new String[]{"3.2", "Honeycomb"};
        else if (sdk == 14) android = new String[]{"4.0", "Ice Cream Sandwich"};
        else if (sdk == 15) android = new String[]{"4.0", "Ice Cream Sandwich"};
        else if (sdk == 16) android = new String[]{"4.1", "Jelly Bean"};
        else if (sdk == 17) android = new String[]{"4.2", "Jelly Bean"};
        else if (sdk == 18) android = new String[]{"4.3", "Jelly Bean"};
        else if (sdk == 19) android = new String[]{"4.4", "KitKat"};
        else if (sdk == 20) android = new String[]{"4.4", "KitKat"};
        else if (sdk == 21) android = new String[]{"5.0", "Lollipop"};
        else if (sdk == 22) android = new String[]{"5.1", "Lollipop"};
        else if (sdk == 23) android = new String[]{"6.0", "Marshmallow"};
        else if (sdk == 24) android = new String[]{"7.0", "Nougat"};
        else if (sdk == 25) android = new String[]{"7.1", "Nougat"};
        else if (sdk == 26) android = new String[]{"8.0", "Oreo"};
        else if (sdk == 27) android = new String[]{"8.1", "Oreo"};
        else if (sdk == 28) android = new String[]{"9", "Pie"};
        else if (sdk == 29) android = new String[]{"10", "Q"};
        else if (sdk == 30) android = new String[]{"11", "R"};
        else if (sdk == 31) android = new String[]{"12", "S"};
        else if (sdk == 32) android = new String[]{"12L", "S"};
        else if (sdk == 33) android = new String[]{"13", "Tiramisu"};
        else if (sdk == 34) android = new String[]{"14", "U"};
        else if (sdk == 35) android = new String[]{"15", "Vanilla"};
        else android = new String[]{"0.0", "-"};

        String name = format
                .replaceAll("%a", "Android")
                .replaceAll("%v", android[0])
                .replaceAll("%c", android[1])
                .replaceAll("%s", String.valueOf(sdk));

        return name.trim();
    }
}
