package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.maxdbmon.extractor.connector.DBMCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * The methods of this class are intended for parsing the result of executing
 * {@link DBMCommand#INFO_LOCKS} command.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class LocksMdbParser extends MdbParser {
    /**
     * Prefix for all keys.
     */
    private static final String PREFIX = "locks_";

    /**
     * Get map of MaxDB locks info.
     *
     * @param result query command result
     * @return map of MaxDB locks info
     */
    @Override
    public Map<String, String> getDataMap(String result) {
        Map<String, String> dataMap = getDataMap(result, "=", null, null);
        Map<String, String> dataMapRes = new HashMap<>();
        for (Map.Entry<String, String> entry : dataMap.entrySet()){
            dataMapRes.put(PREFIX + entry.getKey(), entry.getValue());
        }
        return dataMapRes;
    }
}
