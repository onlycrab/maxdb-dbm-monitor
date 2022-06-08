package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.maxdbmon.extractor.connector.DBMCommand;

import java.util.Map;

/**
 * The methods of this class are intended for parsing the result of executing
 * {@link DBMCommand#INFO_STATE} command.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class StateMdbParser extends MdbParser {
    /**
     * Array of params witch will be excluded from data map.
     */
    private static final String[] EXCLUDE = new String[]{
            "log_max_pages", "log_pages", "log_bytes", "log_percentage", "log_max_bytes"
    };

    /**
     * Get map of MaxDB info state data.
     *
     * @param result query command result
     * @return map of MaxDB info state data
     */
    @Override
    public Map<String, String> getDataMap(String result) {
        return getDataMap(result, "=", EXCLUDE, null);
    }
}
