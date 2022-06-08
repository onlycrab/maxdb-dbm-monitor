package com.github.onlycrab.maxdbmon.extractor.comparator;

import com.github.onlycrab.common.SimpleIniOper;
import com.github.onlycrab.maxdbmon.extractor.parser.ParamsMdbParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for detecting changes to MaxDB instance parameters.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class ParamsComparator {
    /**
     * MaxDB parameters data from previous calculation.
     */
    Map<String, String> previous;

    /**
     * MaxDB parameters data.
     */
    Map<String, String> current;

    /**
     * Create empty object.
     */
    public ParamsComparator(){
        current = new HashMap<>();
        previous = new HashMap<>();
    }

    /**
     * Initialize comparator with previous and current data.
     * <p>Previous data will be read from temporary file. Current data will be taken from MaxDB.
     * After calculating the difference in the data the current data will be stored in the temporary file.</p>
     *
     * <p>If temporary file path starts with "null", then it means that the system property
     * {@code java.io.tmpdir} is {@code null}.</p>
     *
     * @param dbName MaxDB instance name
     * @param result query command result
     * @throws IOException if an I/O error occurs
     * @throws SecurityException if a security manager exists and its does not allow access to {@code java.io.tmpdir}
     *                          system property or file in {@code tmpdir}
     */
    public void init(String dbName, String result) throws IOException, SecurityException {
        String fileName = "maxdbmon.null.params.tmp";
        if (dbName != null){
            if (dbName.trim().length() != 0){
                fileName = "maxdbmon." + dbName.toLowerCase() + ".params.tmp";
            }
        }
        current = new ParamsMdbParser().getDataMap(result);
        String fileAbsPath = SimpleIniOper.getTmpDir() + fileName;
        previous = SimpleIniOper.read(fileAbsPath, true, null);
        SimpleIniOper.write(fileAbsPath, current, null);
    }

    /**
     * Returns difference in parameter map between current and previous state. If some keys are present only
     * in the current or only in the previous data - such key-value pairs are ignored.
     *
     * @return difference if parameter map between current and previous state
     */
    public String getDifference(){
        if (previous == null || current == null){
            return "";
        } else if (previous.size() == 0 || current.size() == 0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String key, valueCurrent, valuePrevious;
        for (Map.Entry<String, String> entry : current.entrySet()){
            key = entry.getKey();
            if (previous.containsKey(key)){
                valueCurrent = entry.getValue();
                valuePrevious = previous.get(key);
                if (!valueCurrent.equals(valuePrevious)){
                    sb.append(printDifference(key, valuePrevious, valueCurrent));
                }
            }
        }
        return sb.toString();
    }

    /**
     * Returns difference between old and new values for specific key.
     *
     * @param key test key
     * @param oldValue previous value
     * @param newValue current value
     * @return difference between old and new values
     */
    String printDifference(String key, String oldValue, String newValue){
        return String.format("<%s> : <%s> => <%s>;", key, oldValue, newValue).replace('"', '\'').replace("\\", "\\\\");
    }
}
