package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.maxdbmon.extractor.connector.DBMCommand;
import com.github.onlycrab.maxdbmon.extractor.state.OperationalState;

/**
 * The methods of this class are intended for parsing the result of executing
 * {@link DBMCommand#OPERATIONAL_STATE} command.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class OperationalStateParser {
    /**
     * Returns MaxDB operational state code.
     *
     * @param result query command result
     * @return MaxDB operational state code
     * @see OperationalState
     */
    public int getOperationalStateCode(String result) {
        String value = getOperationalState(result);
        if (value == null) {
            return OperationalState.UNKNOWN.getCode();
        } else if (value.trim().length() == 0) {
            return OperationalState.UNKNOWN.getCode();
        }
        try {
            return OperationalState.valueOf(value.toUpperCase()).getCode();
        } catch (IllegalArgumentException e) {
            return OperationalState.UNKNOWN.getCode();
        }
    }

    /**
     * Returns string representation of MaxDB operational state.
     *
     * @param result query command result
     * @return string representation of MaxDB operational state
     */
    public static String getOperationalState(String result) {
        if (result == null) {
            return "";
        }
        if (result.trim().length() == 0) {
            return "";
        }
        String[] array = result.split("\n");
        for (int i = array.length - 1; i > -1; i--) {
            if (array[i].length() > 1) {
                return array[i];
            }
        }
        return array[0];
    }
}
