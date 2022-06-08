package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.util.AssertMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link StateMdbParser}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class StateMdbParserTest {
    /**
     * Test for {@link StateMdbParser#getDataMap(String)}.
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    @Test
    public void getDataMap() {
        String data = "Name                    | Value\n" +
                "\n" +
                "Data           (KB)     = 2960\n" +
                "Perm Data      (KB)     = 2904\n" +
                "Data        (Pages)     = 370\n" +
                "Temp Data   (Pages)     = 7\n" +
                "Perm Data       (%)     = 2\n" +
                "Temp Data       (%)     = 0\n" +
                "Sessions Max            = 100\n" +
                "Database Full           = No\n" +
                "Connect Possible        = Yes";

        Map<String, String> expected = new HashMap<>();
        expected.put("data_bytes", String.valueOf(2960 * 1024));
        expected.put("perm_data_bytes", String.valueOf(2904 * 1024));
        expected.put("data_pages", "370");
        expected.put("temp_data_pages", "7");
        expected.put("perm_data_percentage", "2");
        expected.put("temp_data_percentage", "0");
        expected.put("sessions_max", "100");
        expected.put("database_full", "0");
        expected.put("connect_possible", "1");

        StateMdbParser stateMdbParser = new StateMdbParser();
        Map<String, String> actual = stateMdbParser.getDataMap(data);
        assertEquals(expected.size(), actual.size());
        AssertMap.assertContains(expected, actual);

        assertEquals(0, stateMdbParser.getDataMap(null).size());
        assertEquals(0, stateMdbParser.getDataMap("").size());
        assertEquals(0, stateMdbParser.getDataMap("END     \n" + "Name                    | Value\n" +
                "\n").size());
    }
}
