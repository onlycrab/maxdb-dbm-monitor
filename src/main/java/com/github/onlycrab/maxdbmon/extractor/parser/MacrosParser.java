package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.common.SimpleIniOper;

import java.util.HashMap;
import java.util.Map;

public class MacrosParser {
    private static final String DELIMITER = "#";
    private static final String KEY_PREFIX = "zm";

    public Map<String, String> getDataMap(String macros){
        Map<String, String> dataMap = new HashMap<>();
        if (macros == null){
            return dataMap;
        } else if (macros.trim().length() == 0){
            return dataMap;
        }
        for (Map.Entry<String, String> entry : SimpleIniOper.read(macros.split(DELIMITER), false, null).entrySet()){
            dataMap.put(KEY_PREFIX + "_" + entry.getKey(), entry.getValue());
        }
        return dataMap;
    }
}
