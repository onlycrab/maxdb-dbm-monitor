package com.github.onlycrab.maxdbmon.extractor.parser;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link MdbParser}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class MdbParserTest extends MdbParser {
    /**
     * Test for {@link MdbParser#convertBoolean(String)}.
     */
    @Test
    public void convertBoolean(){
        Assert.assertEquals("1", convertBoolean("true"));
        Assert.assertEquals("1", convertBoolean("on"));
        Assert.assertEquals("1", convertBoolean("yes"));
        Assert.assertEquals("0", convertBoolean("false"));
        Assert.assertEquals("0", convertBoolean("off"));
        Assert.assertEquals("0", convertBoolean("no"));
        Assert.assertEquals("", convertBoolean(null));
        Assert.assertEquals("", convertBoolean(""));
        Assert.assertEquals("asd", convertBoolean("asd"));
    }

    /**
     * Test for {@link MdbParser#convertKB(String)};
     */
    @Test
    public void convertKB() {
        assertEquals("10240", convertKB( "10"));
        assertEquals("0", convertKB( "0"));
        assertEquals("-1", convertKB( "-24"));
        assertEquals("-1", convertKB( ""));
        assertEquals("-1", convertKB( null));
    }

    /**
     * Test for {@link MdbParser#convertKey(String, String[]...)}.
     */
    @Test
    public void convertKey(){
        MdbParserTest parserTest = new MdbParserTest();
        assertEquals("data_bytes", parserTest.convertKey("Data (KB)", new String[]{"(kb)", "bytes"}));
        assertEquals("data_pages", parserTest.convertKey("Data (Pages)", new String[]{"(pages)", "pages"}));
        assertEquals("temp_data_percentage", parserTest.convertKey("Temp Data (%)", new String[]{"(%)", "percentage"}));
        assertEquals("some_percentage_for_bytes_like_0", parserTest.convertKey("Some (%) for (KB) like null",
                new String[]{"(kb)", "bytes"}, new String[]{"(%)", "percentage"}, new String[]{"null", "0"}));
    }

    @Override
    public Map<String, String> getDataMap(String result) {
        return null;
    }
}
