package com.github.onlycrab.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Common utilities.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class UtilitiesString {
    /**
     * Trim duplicate spaces and tab characters from string {@code str}.
     * <p>The method replaces all consecutive space and tab characters with a single space character.</p>
     *
     * @param str          target string
     * @param standardTrim also use {@link String#trim()}.
     * @return trimmed string
     */
    public static String trimDuplicateSpaces(String str, boolean standardTrim) {
        if (str == null) {
            return "";
        }
        String result = str.replace("\t", " ");
        while (result.contains("  ")) {
            result = result.replace("  ", " ");
        }
        if (standardTrim) {
            return result.trim();
        } else {
            return result;
        }
    }

    /**
     * Remove prefix from string.
     * If {@code recursive} is {@code true}, the method will call itself until all prefixes are removed.
     * For example:
     * {@code removePrefix("PrefPrefStr", "Pref", false)} will return {@code "PrefStr"};
     * {@code removePrefix("PrefPrefStr", "Pref", true)} will return {@code "Str"}.
     *
     * @param target    string for prefix removing
     * @param prefix    value to remove from string
     * @param recursive whether to execute the method recursively
     * @return target string without prefix
     */
    public static String removePrefix(String target, String prefix, boolean recursive) {
        if (target == null || prefix == null) {
            return target;
        }
        if (target.equals(prefix)) {
            return "";
        }
        if (!recursive) {
            if (target.indexOf(prefix) == 0) {
                return target.substring(prefix.length());
            } else {
                return target;
            }
        } else {
            String result = target;
            while (result.indexOf(prefix) == 0) {
                result = result.substring(prefix.length());
            }
            return result;
        }
    }

    /**
     * Returns whether the string contains only digits.
     *
     * @param str string for check
     * @return {@code true} if {@code str} contains only digits, otherwise return {@code false}.
     */
    public static boolean isOnlyDigits(String str) {
        if (str == null) {
            return false;
        }
        if (str.trim().length() == 0) {
            return false;
        }
        return Pattern.compile("\\d+").matcher(str).matches();
    }

    /**
     * Parse string to list by regular expression.
     *
     * @param target string for parsing
     * @param regex  regular expression
     * @return parsed list
     */
    public static List<String> parseToList(String target, String regex) {
        List<String> list = new ArrayList<>();
        if (target == null) return list;
        if (target.trim().length() == 0) return list;
        Collections.addAll(list, target.split(regex));
        return list;
    }
}
