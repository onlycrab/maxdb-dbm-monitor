package com.github.onlycrab.util;

import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Asserts class for {@code Map<String, String>}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
@SuppressWarnings("SuspiciousMethodCalls")
public class AssertMap {
    /**
     * Check if any key-value pair in {@code actual} exists in {@code expected}.
     *
     * @param expected standard map
     * @param actual test map
     */
    public static void assertContains(Map<String, String> expected, Map<String, String> actual){
        for (HashMap.Entry entry : expected.entrySet()) {
            if (!actual.containsKey(entry.getKey())){
                Assert.fail(String.format("Actual map not contains key <%s>", entry.getKey()));
            } else if (!entry.getValue().equals(actual.get(entry.getKey()))){
                Assert.fail(String.format("Actual map by key <%s> : value <%s> not equals to <%s>.",
                        entry.getKey(), actual.get(entry.getKey()), entry.getValue()));
            }
        }
    }
}
