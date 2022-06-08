package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.util.AssertMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link CachesMdbParser}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class CachesMdbParserTest {
    /**
     * Test for {@link CachesMdbParser#getDataMap(String)}.
     */
    @Test
    public void getDataMap() {
        String data = "Type                 | Accesses             | Successful           | Unsuccessful         | Hit Rate (%)\n" +
                "\n" +
                "DATA                 | 233915302            | 226345572            | 7569730              | 97          \n" +
                "SEQUENCE             | 0                    | 0                    | 0                    | (null)      \n" +
                "COMMANDPREPARE       | 430097               | 424703               | 10784                | 98          \n" +
                "COMMANDEXECUTE       | 2838146              | 2838136              | 10                   | 99          \n" +
                "CATALOGCACHE         | 416026               | 371614               | 44412                | 9           \n" +
                "CATALOG              | 0                    | 0                    | 0                    | (null)      \n" +
                "CATALOG              | 0                    | 0                    | 0                    | (null)      \n";

        Map<String, String> expected = new HashMap<>();
        expected.put("cache_hitrate_data", "97");
        expected.put("cache_hitrate_sequence", "0");
        expected.put("cache_hitrate_commandprepare", "98");
        expected.put("cache_hitrate_commandexecute", "99");
        expected.put("cache_hitrate_catalogcache", "9");

        CachesMdbParser cachesMdbParser = new CachesMdbParser();
        Map<String, String> actual = cachesMdbParser.getDataMap(data);
        assertEquals(expected.size(), actual.size());
        AssertMap.assertContains(expected, actual);

        assertEquals(0, cachesMdbParser.getDataMap(null).size());
        assertEquals(0, cachesMdbParser.getDataMap("").size());
        assertEquals(0, cachesMdbParser.getDataMap("Name                    | Value\n").size());
    }
}
