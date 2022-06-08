package com.github.onlycrab.common;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for {@link UtilitiesString}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class UtilitiesStringTest {
    /**
     * Test for {@link UtilitiesString#trimDuplicateSpaces(String, boolean)}.
     */
    @Test
    public void trimDuplicateSpaces() {
        String[][] data = {
                {null, "", ""},
                {"", "", ""},
                {"getXServerMap", "getXServerMap", "getXServerMap"},
                {"   getXServerMap", " getXServerMap", "getXServerMap"},
                {"t est   ", "t est ", "t est"},
                {"getXServerMap  ", "getXServerMap ", "getXServerMap"},
                {"te     st", "te st", "te st"},
                {"      te      st          ", " te st ", "te st"}
        };
        for (String[] test : data) {
            Assert.assertEquals(test[1], UtilitiesString.trimDuplicateSpaces(test[0], false));
            assertEquals(test[2], UtilitiesString.trimDuplicateSpaces(test[0], true));
        }
    }

    /**
     * Test for {@link UtilitiesString#removePrefix(String, String, boolean)}.
     */
    @Test
    public void removePrefix() {
        String[][] data = {
                {"0", "350", "350", "350"},
                {"0", "000350", "00350", "350"},
                {"pref", "prefstrpref", "strpref", "strpref"},
                {"pref", "prefprefstrpref", "prefstrpref", "strpref"}
        };
        for (String[] test : data) {
            assertEquals(test[2], UtilitiesString.removePrefix(test[1], test[0], false));
            assertEquals(test[3], UtilitiesString.removePrefix(test[1], test[0], true));
        }
    }

    /**
     * Test for {@link UtilitiesString#isOnlyDigits(String)}.
     */
    @Test
    public void isOnlyDigits() {
        assertTrue(UtilitiesString.isOnlyDigits("123"));
        assertFalse(UtilitiesString.isOnlyDigits(" 123"));
        assertFalse(UtilitiesString.isOnlyDigits("1s23"));
        assertFalse(UtilitiesString.isOnlyDigits("123 "));
        assertFalse(UtilitiesString.isOnlyDigits(""));
        assertFalse(UtilitiesString.isOnlyDigits(null));
    }
}