package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.util.AssertMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for {@link MacrosParser}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class MacrosParserTest {
    /**
     * Test for {@link MacrosParser#getDataMap(String)}.
     */
    @Test
    public void getDataMap(){
        String data = "mac1=val1#mac2=#val3#=val4#mac5=val5";

        Map<String, String> expected = new HashMap<>();
        expected.put("zm_mac1", "val1");
        expected.put("zm_mac2", "");
        expected.put("zm_mac5", "val5");

        MacrosParser macrosParser = new MacrosParser();
        Map<String, String> actual = macrosParser.getDataMap(data);
        Assert.assertEquals(expected.size(), actual.size());
        AssertMap.assertContains(expected, actual);

        Assert.assertEquals(0, macrosParser.getDataMap(null).size());
        Assert.assertEquals(0, macrosParser.getDataMap("").size());
    }
}
