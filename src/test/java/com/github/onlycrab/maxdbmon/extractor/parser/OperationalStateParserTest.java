package com.github.onlycrab.maxdbmon.extractor.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link OperationalStateParser}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class OperationalStateParserTest {
    /**
     * Test for {@link OperationalStateParser#getOperationalStateCode(String)}.
     */
    @Test
    public void getOperationalStateCode() {
        OperationalStateParser operationalStateParser = new OperationalStateParser();
        assertEquals(0, operationalStateParser.getOperationalStateCode("State\nOFFLINE"));
        assertEquals(1, operationalStateParser.getOperationalStateCode("State\nONLINE"));
        assertEquals(2, operationalStateParser.getOperationalStateCode("State\nADMIN"));
        assertEquals(3, operationalStateParser.getOperationalStateCode("State\nSTANDBY"));
        assertEquals(4, operationalStateParser.getOperationalStateCode("State\n"));
        assertEquals(4, operationalStateParser.getOperationalStateCode(""));
        assertEquals(4, operationalStateParser.getOperationalStateCode(null));
    }

    /**
     * Test for {@link OperationalStateParser#getOperationalState(String)}.
     */
    @Test
    public void getOperationalState() {
        assertEquals("OFFLINE", OperationalStateParser.getOperationalState("State\nOFFLINE"));
        assertEquals("ONLINE", OperationalStateParser.getOperationalState("State\nONLINE"));
        assertEquals("ADMIN", OperationalStateParser.getOperationalState("State\nADMIN"));
        assertEquals("STANDBY", OperationalStateParser.getOperationalState("State\nSTANDBY"));
        assertEquals("State", OperationalStateParser.getOperationalState("State\n"));
        assertEquals("", OperationalStateParser.getOperationalState(""));
        assertEquals("", OperationalStateParser.getOperationalState(null));
    }
}
