package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.util.AssertMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link ParamsMdbParser}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class ParamsMdbParserTest {
    /**
     * Test for {@link ParamsMdbParser#getDataMap(String)}.
     */
    @Test
    public void getDataMap(){
        String data = "Description                      | Value                                 \n" +
                "MaxLogWriterTasks                | 0                                       \n" +
                "_MAX_BACKUP_TASKS                | 0                                       \n" +
                "KernelVersion                    | KERNEL    7.9.08   BUILD 008-123-247-140\n" +
                "KERNELVERSION                    | KERNEL    7.9.08   BUILD 008-123-247-140\n" +
                "EnableVariableOutput             | NO                                      \n" +
                "EnableVariableInput              | YES                                     \n";

        Map<String, String> expected = new HashMap<>();
        expected.put("description", "value");
        expected.put("maxlogwritertasks", "0");
        expected.put("_max_backup_tasks", "0");
        expected.put("kernelversion", "kernel 7.9.08 build 008-123-247-140");
        expected.put("enablevariableoutput", "0");
        expected.put("enablevariableinput", "1");

        ParamsMdbParser paramsMdbParser = new ParamsMdbParser();
        Map<String, String> actual = paramsMdbParser.getDataMap(data);
        assertEquals(expected.size(), actual.size());
        AssertMap.assertContains(expected, actual);

        assertEquals(0, paramsMdbParser.getDataMap(null).size());
        assertEquals(0, paramsMdbParser.getDataMap("").size());
        assertEquals(1, paramsMdbParser.getDataMap("Name                    | Value\n").size());
    }
}
