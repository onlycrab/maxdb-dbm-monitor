package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.maxdbmon.extractor.connector.DBMCommand;

import java.util.Map;

/**
 * The methods of this class are intended for parsing the result of executing
 * {@link DBMCommand#INFO_PARAMS} command.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class ParamsMdbParser extends MdbParser {
    @Override
    public Map<String, String> getDataMap(String result) {
        return getDataMap(result, "\\|", null, null);
    }
}
