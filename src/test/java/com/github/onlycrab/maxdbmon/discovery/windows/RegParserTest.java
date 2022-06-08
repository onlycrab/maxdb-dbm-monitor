package com.github.onlycrab.maxdbmon.discovery.windows;

import com.github.onlycrab.maxdbmon.discovery.DiscoveryException;
import com.github.onlycrab.maxdbmon.discovery.DiscoveryTest;
import com.github.onlycrab.util.TestFileReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Test class for {@link RegParser}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class RegParserTest {
    /**
     * Test for {@link RegParser#getMaxDBList(List)}.
     */
    @Test
    public void getMaxDBList() {
        List<String> res;
        try {
            res = RegParser.getMaxDBList(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_maxdb_typical")));
            Assert.assertArrayEquals(new String[]{"CDB", "SDB"}, res.toArray());

            res = RegParser.getMaxDBList(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_maxdb_wrong_format")));
            Assert.assertArrayEquals(new String[]{"SDB"}, res.toArray());

            res = RegParser.getMaxDBList(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_maxdb_missing")));
            Assert.assertArrayEquals(new String[]{}, res.toArray());
        } catch (DiscoveryException e) {
            Assert.fail("DiscoveryException : " + e.getMessage());
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        }
    }

    /**
     * Test for {@link RegParser#getXServerMap(List)}.
     */
    @Test
    public void getXServerMap() {
        Map<String, Integer> res;
        try {
            res = RegParser.getXServerMap(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_xserver_typical")));
            Assert.assertEquals(2, res.size());
            if (!res.containsKey("SDB")) {
                Assert.fail("XServer-SDB-7201 not found");
            } else {
                Assert.assertEquals(new Integer(7201), res.get("SDB"));
            }
            if (!res.containsKey("CDB")) {
                Assert.fail("XServer-SDB-7203 not found");
            } else {
                Assert.assertEquals(new Integer(7203), res.get("CDB"));
            }

            res = RegParser.getXServerMap(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_xserver_missing")));
            Assert.assertEquals(0, res.size());

            res = RegParser.getXServerMap(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_xserver_wrong_path")));
            Assert.assertEquals(0, res.size());
        } catch (DiscoveryException e) {
            Assert.fail("DiscoveryException : " + e.getMessage());
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        }
    }
}
