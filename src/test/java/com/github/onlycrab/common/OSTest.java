package com.github.onlycrab.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link OS}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class OSTest {

    /**
     * Test for {@link OS#getOSType(boolean)}.
     */
    @Test
    public void getOSType() {
        String testKey = "os.name";
        String os = null;
        try {
            os = System.getProperty(testKey);

            System.setProperty(testKey, "Some WINdows OS");
            Assert.assertEquals(OS.OSType.WINDOWS, OS.getOSType(false));

            System.setProperty(testKey, "Linux server OS");
            Assert.assertEquals(OS.OSType.LINUX, OS.getOSType(false));

            System.setProperty(testKey, "this is unix");
            Assert.assertEquals(OS.OSType.UNIX, OS.getOSType(false));

            System.setProperty(testKey, "new super OS");
            Assert.assertEquals(OS.OSType.UNKNOWN, OS.getOSType(false));
        } catch (SecurityException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            if (os != null) {
                System.setProperty(testKey, os);
            }
        }
    }

    /**
     * Test for {@link OS#getSystemValue(String, boolean)}.
     */
    @Test
    public void getSystemValue() {
        String testKey = "os.name.getXServerMap";
        String testValue = "some os name";
        try {
            System.setProperty(testKey, testValue);
        } catch (SecurityException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return;
        }
        Assert.assertEquals(testValue, OS.getSystemValue(testKey, false));
    }
}
