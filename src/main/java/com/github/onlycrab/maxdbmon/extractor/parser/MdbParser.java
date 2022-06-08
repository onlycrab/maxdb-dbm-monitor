package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.common.UtilitiesString;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Common class for parsing result of SAPDBC query command.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
@SuppressWarnings("WeakerAccess,CanBeFinal")
public abstract class MdbParser {
    /**
     * String to Numeric conversion table.
     */
    protected final String[][] TRANS_FROM_DATATABLE = {
            {"yes", "1"},
            {"no", "0"},
            {"on", "1"},
            {"off", "0"},
            {"true", "1"},
            {"false", "0"}
    };
    protected int columnsCount = 2;
    protected int keyColumn = 0;
    protected int valueColumn = 1;

    /**
     * Get map of MaxDB info data.
     *
     * @param result query command result
     * @return map of MaxDB info data.
     */
    public abstract Map<String, String> getDataMap(String result);

    /**
     * Get map of MaxDB info data.
     *
     * @param result query command result
     * @param delimiter delimiter for key between value
     * @param exclude keys for exclude from data map
     * @param only only these keys will be in data map
     * @return map of MaxDB info data.
     */
    protected final Map<String, String> getDataMap(String result, String delimiter, @Nullable String[] exclude, @Nullable String[] only) {
        HashMap<String, String> pairs = new HashMap<>();
        if (result == null || delimiter == null) {
            return pairs;
        }
        if (result.trim().equals("")) {
            return pairs;
        }
        List<String> excludeList;
        List<String> onlyList;
        if (exclude != null){
            excludeList = Arrays.asList(exclude);
        } else {
            excludeList = new ArrayList<>();
        }
        if (only != null){
            onlyList = Arrays.asList(only);
        } else {
            onlyList = new ArrayList<>();
        }

        String[] array = result.split("\n");
        for (String str : array) {
            String[] pairStr = str.split(delimiter);
            if (pairStr.length != columnsCount) {
                continue;
            }
            pairStr[keyColumn] = UtilitiesString.trimDuplicateSpaces(pairStr[keyColumn], true).toLowerCase();
            pairStr[valueColumn] = UtilitiesString.trimDuplicateSpaces(pairStr[valueColumn], true).toLowerCase();
            if (pairStr[keyColumn].length() < 3 || pairStr[valueColumn].length() == 0) {
                continue;
            }

            if (pairStr[keyColumn].contains("(kb)")){
                pairStr[valueColumn] = convertKB(pairStr[valueColumn]);
            }
            pairStr[keyColumn] = convertKey(pairStr[keyColumn], new String[]{"(kb)", "bytes"}, new String[]{"(%)", "percentage"},
                    new String[]{"(pages)", "pages"}, new String[]{"(seconds)", "seconds"});

            if (onlyList.size() > 0){
                if (!onlyList.contains(pairStr[keyColumn])){
                    continue;
                }
            }
            if (excludeList.size() > 0){
                if (excludeList.contains(pairStr[keyColumn])){
                    continue;
                }
            }

            if (!UtilitiesString.isOnlyDigits(pairStr[valueColumn])) {
                pairs.put(pairStr[keyColumn], convertBoolean(pairStr[valueColumn]));
            } else {
                pairs.put(pairStr[keyColumn], pairStr[valueColumn]);
            }
        }
        return pairs;
    }

    /**
     * Convert boolean value to integer 0 or 1.
     *
     * @param value string representation of boolean for conversion
     * @return boolean as integer 0 or 1.
     */
    protected final String convertBoolean(String value){
        if (value == null){
            return "";
        }
        for (String[] transform : TRANS_FROM_DATATABLE) {
            if (value.toLowerCase().equals(transform[0])) {
                return transform[1];
            }
        }
        return value;
    }

    /**
     * Returns number of bytes from KBytes string.
     *
     * @param kbValue string that contains KBytes number
     * @return number of bytes from KBytes string
     */
    protected final String convertKB(String kbValue) {
        long bytes = -1;
        try {
            bytes = Long.parseLong(kbValue) * 1024;
            if (bytes < 0){
                bytes = -1;
            }
        } catch (NumberFormatException ignore) {
        }
        return String.valueOf(bytes);
    }

    /**
     * Convert key value.
     * The keys are converted as follows:
     * <blockquote>
     * - all characters are converted to lower case
     * - all consecutive space characters are replaced by {@code _}
     * - all occurrences of {@code search} value (first element in pair {@code srPair}) are replaced
     * by {@code replace} (second element in pair {@code srPair})
     * </blockquote>
     *
     * @param key     string value of key
     * @param srPairs pairs of {@code search} and {@code replace} values
     * @return converted key value
     */
    protected final String convertKey(String key, String[]... srPairs) {
        if (key == null) {
            return "";
        }
        String result = key.toLowerCase().replace(" ", "_").replace(".", "");
        if (srPairs == null){
            return result;
        }
        for (String[] srPair : srPairs) {
            if (srPair == null) {
                continue;
            } else if (srPair.length != 2) {
                continue;
            } else if (srPair[0] == null || srPair[1] == null) {
                continue;
            }
            if (result.contains(srPair[0])) {
                result = result.replace(srPair[0], srPair[1]);
            }
        }
        return result;
    }
}
