package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.common.UtilitiesString;
import com.github.onlycrab.maxdbmon.extractor.connector.DBMCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * The methods of this class are intended for parsing the result of executing
 * {@link DBMCommand#INFO_CACHES} command.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class CachesMdbParser extends MdbParser {
    /**
     * Array of all available params.
     */
    private static final String[] ONLY = new String[]{"data", "sequence", "commandprepare", "commandexecute", "catalogcache"};

    /**
     * Prefix for all keys.
     */
    private static final String PREFIX = "cache_hitrate_";

    public CachesMdbParser(){
        super.columnsCount = 5;
        super.valueColumn = 4;
    }

    /**
     * Get map of MaxDB info caches hit rate.
     *
     * @param result query command result
     * @return map of MaxDB info caches  hit rate
     */
    @Override
    public Map<String, String> getDataMap(String result) {
        Map<String, String> dataMap = getDataMap(result, "\\|", null, ONLY);
        Map<String, String> dataMapRes = new HashMap<>();
        for (Map.Entry<String, String> entry : dataMap.entrySet()){
            if (UtilitiesString.isOnlyDigits(entry.getValue())) {
                dataMapRes.put(PREFIX + entry.getKey(), entry.getValue());
            } else {
                dataMapRes.put(PREFIX + entry.getKey(), "0");
            }
        }
        return dataMapRes;
    }
}
