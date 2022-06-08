package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.util.AssertMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link LogMdbParser}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class LogMdbParserTest {
    /**
     * Test for {@link LogMdbParser#getDataMap(String)}.
     */
    @Test
    public void getDataMap() {
        String data = "Name                            | Value\n" +
                "\n" +
                "Log Mirrored                    = NO\n" +
                "Log Writing                     = ON\n" +
                "Log Automatic Overwrite         = OFF\n" +
                "Max. Size (KB)                  = 11249328\n" +
                "Backup Segment Size (KB)        = 341328\n" +
                "Backup Interval (seconds)       = 7200\n" +
                "Used Size (KB)                  = 73168\n" +
                "Used Size (%)                   = 1\n" +
                "Not Saved (KB)                  = 73168\n" +
                "Not Saved (%)                   = 1\n" +
                "Log Since Last Data Backup (KB) = 0\n" +
                "Savepoints                      = 61757\n" +
                "Checkpoints                     = 0\n" +
                "Physical Reads                  = 959686\n" +
                "Physical Writes                 = 2435661\n" +
                "Queue Size (KB)                 = 1600\n" +
                "Queue Overflows                 = 48425\n" +
                "Group Commits                   = 41\n" +
                "Waits for Logwriter             = 226759\n" +
                "Max. Waits                      = 2\n" +
                "Average Waits                   = 0\n" +
                "OMS Log Used Pages              = 0\n" +
                "OMS Min. Free Pages             = 0";

        Map<String, String> expected = new HashMap<>();
        expected.put("log_mirrored", "0");
        expected.put("log_writing", "1");
        expected.put("log_automatic_overwrite", "0");
        expected.put("log_max_size_bytes", "11519311872");
        expected.put("log_used_size_bytes", "74924032");
        expected.put("log_used_size_percentage", "1");
        expected.put("log_backup_interval_seconds", "7200");

        LogMdbParser logMdbParser = new LogMdbParser();
        Map<String, String> actual = logMdbParser.getDataMap(data);
        assertEquals(expected.size(), actual.size());
        AssertMap.assertContains(expected, actual);

        assertEquals(0, logMdbParser.getDataMap(null).size());
        assertEquals(0, logMdbParser.getDataMap("").size());
        assertEquals(0, logMdbParser.getDataMap("Name                    | Value\n").size());
    }
}
