package io.github.itzispyder.impropers3dminimap.util.misc;

import java.math.BigInteger;
import java.util.UUID;

public final class StringUtils {

    public static String color(String s) {
        return s.replace('&', '§');
    }

    public static String decolor(String s) {
        return s.replaceAll("[§|&][1234567890abcdefklmnor]", "");
    }

    public static boolean isNumber(String str) {
        return str.matches("^-?\\d*\\.?\\d*$");
    }

    public static String format(String s, Object... args) {
        for (int i = 0; i < args.length; i++) {
            Object obj = args[i];
            String arg = obj == null ? "null" : obj.toString();
            s = s.replaceAll("%" + i, arg);
            s = s.replaceFirst("%s", arg);
        }
        return s.replace("%n", "\n");
    }

    public static String capitalize(String s) {
        if (s.length() <= 1) return s.toUpperCase();
        s = s.toLowerCase();
        return String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1);
    }

    public static String capitalizeWords(String s) {
        s = s.replaceAll("[_-]"," ");
        String[] sArray = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String str : sArray) sb.append(capitalize(str)).append(" ");
        return sb.toString().trim();
    }

    public static UUID toUUID(String uuid) {
        uuid = uuid.trim().replace("-", "");
        return new UUID(
                new BigInteger(uuid.substring(0, 16), 16).longValue(),
                new BigInteger(uuid.substring(16), 16).longValue()
        );
    }

    public static String keyPressWithShift(String s) {
        return s.length() != 1 ? s : Character.toString(charWithShift(s.charAt(0)));
    }

    private static char charWithShift(char c) {
        char upper = Character.toUpperCase(c);
        if (upper != c) {
            return upper;
        }

        switch (c) {
            case '1' -> upper = '!';
            case '2' -> upper = '@';
            case '3' -> upper = '#';
            case '4' -> upper = '$';
            case '5' -> upper = '%';
            case '6' -> upper = '^';
            case '7' -> upper = '&';
            case '8' -> upper = '*';
            case '9' -> upper = '(';
            case '0' -> upper = ')';
            case '-' -> upper = '_';
            case '=' -> upper = '+';
            case '`' -> upper = '~';
            case '[' -> upper = '{';
            case ']' -> upper = '}';
            case '\\' -> upper = '|';
            case '\'' -> upper = '"';
            case ';' -> upper = ':';
            case '/' -> upper = '?';
            case '.' -> upper = '>';
            case ',' -> upper = '<';
        }

        return upper;
    }
}
