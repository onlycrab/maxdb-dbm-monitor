package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.maxdbmon.extractor.connector.DBMCommand;

/**
 * The methods of this class are intended for parsing the result of executing
 * {@link DBMCommand#INFO_DATA} command.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class InfoDataParser {
    private static final String VOLUME_INDICATOR = "Devspace Name";
    /**
     * Returns count of volumes in DataArea.
     *
     * @param result query command result
     * @return count of volumes in DataArea
     */
    public int getDataVolumes(String result){
        if (result == null){
            return 0;
        }
        int count = 0;
        for (String s : result.split("\n")){
            if (s.length() > VOLUME_INDICATOR.length()){
                if (s.startsWith(VOLUME_INDICATOR)){
                    count++;
                }
            }
        }
        return count;
    }
}
