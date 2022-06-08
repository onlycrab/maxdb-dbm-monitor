package com.github.onlycrab.maxdbmon.extractor.parser;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link InfoDataParser}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class InfoDataParserTest {
    /**
     * Test for {@link InfoDataParser#getDataVolumes(String)}.
     */
    @Test
    public void getDataVolumes(){
        InfoDataParser infoDataParser = new InfoDataParser();
        Assert.assertEquals(0, infoDataParser.getDataVolumes(null));
        Assert.assertEquals(0, infoDataParser.getDataVolumes(""));
        Assert.assertEquals(1, infoDataParser.getDataVolumes("Devspace Name = D:\\sapdb\\AAA\\sapdata\\DISKD0001"));
        Assert.assertEquals(2, infoDataParser.getDataVolumes("Name                | Value\n" +
                "\n" +
                "Devspace Name       = D:\\sapdb\\ABC\\sapdata\\DISKD0001\n" +
                "   Total Space (KB) = 51200000\n" +
                "   Used Space (KB)  = 51199984\n" +
                "   Used Space (%)   = 100\n" +
                "   Free Space (KB)  = 16\n" +
                "   Free Space (%)   = 0\n" +
                "Devspace Name       = E:\\sapdb\\DEF\\sapdata\\DISKD0002\n" +
                "   Total Space (KB) = 100000000\n" +
                "   Used Space (KB)  = 99999496\n" +
                "   Used Space (%)   = 99\n" +
                "   Free Space (KB)  = 504\n" +
                "   Free Space (%)   = 1"
        ));
    }
}
