package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.util.AssertMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link LocksMdbParser}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class LocksMdbParserTest {
    /**
     * Test for {@link LocksMdbParser#getDataMap(String)}.
     */
    @Test
    public void getDataMap() {
        String data = "Name                 | Value\n" +
                "\n" +
                "Max. Entries         = 3602400\n" +
                "Average Used Entries = 2\n" +
                "Collisions           = 0\n" +
                "Escalations          = 0\n" +
                "Row Locks            = 13066\n" +
                "Table Locks          = 32597\n" +
                "Request Timeout      = 3600";

        Map<String, String> expected = new HashMap<>();
        expected.put("locks_max_entries", "3602400");
        expected.put("locks_average_used_entries", "2");
        expected.put("locks_collisions", "0");
        expected.put("locks_escalations", "0");
        expected.put("locks_row_locks", "13066");
        expected.put("locks_table_locks", "32597");
        expected.put("locks_request_timeout", "3600");

        LocksMdbParser locksMdbParser = new LocksMdbParser();
        Map<String, String> actual = locksMdbParser.getDataMap(data);
        assertEquals(expected.size(), actual.size());
        AssertMap.assertContains(expected, actual);

        assertEquals(0, locksMdbParser.getDataMap(null).size());
        assertEquals(0, locksMdbParser.getDataMap("").size());
        assertEquals(0, locksMdbParser.getDataMap("Name                    | Value\n").size());
    }
}
