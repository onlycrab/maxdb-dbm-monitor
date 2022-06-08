package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.maxdbmon.extractor.connector.DBMCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * The methods of this class are intended for parsing the result of executing
 * {@link DBMCommand#INFO_LOG} command.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class LogMdbParser extends MdbParser {
    /**
     * Array of all available params.
     */
    private static final String[] ONLY = new String[]{"log_mirrored", "log_writing", "log_automatic_overwrite", "max_size_bytes",
            "used_size_bytes", "used_size_percentage", "backup_interval_seconds"};

    /**
     * Prefix for all keys.
     */
    private static final String PREFIX = "log_";

    /**
     * Get map of MaxDB info caches hit rate.
     *
     * @param result query command result
     * @return map of MaxDB info caches  hit rate
     */
    @Override
    public Map<String, String> getDataMap(String result) {
        Map<String, String> dataMap = getDataMap(result, "=", null, ONLY);
        Map<String, String> dataMapRes = new HashMap<>();
        for (Map.Entry<String, String> entry : dataMap.entrySet()){
            if (entry.getKey().length() > 4){
                if (entry.getKey().startsWith("log_")){
                    dataMapRes.put(entry.getKey(), entry.getValue());
                } else {
                    dataMapRes.put(PREFIX + entry.getKey(), entry.getValue());
                }
            }
        }
        return dataMapRes;
    }
}
