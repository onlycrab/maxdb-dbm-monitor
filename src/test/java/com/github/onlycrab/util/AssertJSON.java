package com.github.onlycrab.util;

import org.junit.Assert;

/**
 * Asserts class for JSON string.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class AssertJSON {
    /**
     * Check if string {@code JSON} contains an element {@code "key":"value"}.
     *
     * @param JSON string in JSON format
     * @param key test key
     * @param value test value
     */
    public static void assertContains(String JSON, String key, String value){
        if (JSON == null){
            Assert.fail("JSON is <null>");
        } else if (key == null){
            Assert.fail("Key is <null>");
        } else if (value == null){
            Assert.fail("Value is <null>");
        }
        String test = String.format("\"%s\":\"%s\"", key, value);
        if (!JSON.contains(test)){
            Assert.fail(String.format("JSON %s not contains <%s>", JSON, test));
        }
    }
}
