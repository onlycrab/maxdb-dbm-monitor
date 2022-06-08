package com.github.onlycrab.maxdbmon.discovery;

import com.github.onlycrab.util.TestFileReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * Test class for {@link Discovery}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class DiscoveryTest {
    /**
     * Test for {@link Discovery#getMaxDBLinux()}.
     */
    @Test
    public void getMaxDBLinux() {
        String actual;
        Discovery discovery;
        try {
            discovery = new Discovery();
        } catch (Exception e) {
            Assert.fail("Unexpected critical exception occurred : " + e.getMessage());
            return;
        }
        try {
            discovery = new Discovery();

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_typical")), null, Discovery.MODE_NAMES_NONE);
            actual = discovery.getMaxDBLinux();
            assertEquals("{\"data\":[{\"{#MAXDBNAME}\":\"SDB\"}, " +
                    "{\"{#MAXDBNAME}\":\"CDB\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_typical")), "SDB", Discovery.MODE_NAMES_EXCLUDE);
            actual = discovery.getMaxDBLinux();
            assertEquals("{\"data\":[{\"{#MAXDBNAME}\":\"CDB\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_typical")), "CDB,SDB", Discovery.MODE_NAMES_EXCLUDE);
            actual = discovery.getMaxDBLinux();
            assertEquals("{\"data\":[]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_typical")), "SDB", Discovery.MODE_NAMES_ONLY);
            actual = discovery.getMaxDBLinux();
            assertEquals("{\"data\":[{\"{#MAXDBNAME}\":\"SDB\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_typical")), "CDB,SDB", Discovery.MODE_NAMES_ONLY);
            actual = discovery.getMaxDBLinux();
            assertEquals("{\"data\":[{\"{#MAXDBNAME}\":\"SDB\"}, " +
                    "{\"{#MAXDBNAME}\":\"CDB\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_typical")), "ADB", Discovery.MODE_NAMES_ONLY);
            actual = discovery.getMaxDBLinux();
            assertEquals("{\"data\":[]}", actual);
        } catch (DiscoveryException e) {
            Assert.fail("DiscoveryException : " + e.getMessage());
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        }

        try {
            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_installations_missing")), null, Discovery.MODE_NAMES_NONE);
            actual = discovery.getMaxDBLinux();
            Assert.fail(String.format("Missing DiscoveryException, result : <%s>.", actual));
        } catch (DiscoveryException ignored) {
            //ok
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        }
    }

    /**
     * Test for {@link Discovery#getMaxDBWindows()}.
     */
    @Test
    public void getMaxDBWindows() {
        String actual;
        Discovery discovery;
        try {
            discovery = new Discovery();
        } catch (Exception e) {
            Assert.fail("Unexpected critical exception occurred : " + e.getMessage());
            return;
        }

        try {
            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_maxdb_typical")), null, Discovery.MODE_NAMES_NONE);
            actual = discovery.getMaxDBWindows();
            assertEquals("{\"data\":[{\"{#MAXDBNAME}\":\"CDB\"}, " +
                    "{\"{#MAXDBNAME}\":\"SDB\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_maxdb_typical")), "SDB", Discovery.MODE_NAMES_EXCLUDE);
            actual = discovery.getMaxDBWindows();
            assertEquals("{\"data\":[{\"{#MAXDBNAME}\":\"CDB\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_maxdb_typical")), "SDB,CDB", Discovery.MODE_NAMES_EXCLUDE);
            actual = discovery.getMaxDBWindows();
            assertEquals("{\"data\":[]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_maxdb_typical")), "SDB", Discovery.MODE_NAMES_ONLY);
            actual = discovery.getMaxDBWindows();
            assertEquals("{\"data\":[{\"{#MAXDBNAME}\":\"SDB\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_maxdb_typical")), "CDB,SDB", Discovery.MODE_NAMES_ONLY);
            actual = discovery.getMaxDBWindows();
            assertEquals("{\"data\":[{\"{#MAXDBNAME}\":\"CDB\"}, " +
                    "{\"{#MAXDBNAME}\":\"SDB\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_maxdb_typical")), "XDB", Discovery.MODE_NAMES_ONLY);
            actual = discovery.getMaxDBWindows();
            assertEquals("{\"data\":[]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_maxdb_wrong_format")), null, Discovery.MODE_NAMES_NONE);
            actual = discovery.getMaxDBWindows();
            assertEquals("{\"data\":[{\"{#MAXDBNAME}\":\"SDB\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_maxdb_missing")), null, Discovery.MODE_NAMES_NONE);
            actual = discovery.getMaxDBWindows();
            assertEquals("{\"data\":[]}", actual);

        } catch (DiscoveryException e) {
            Assert.fail("DiscoveryException : " + e.getMessage());
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        }
    }

    /**
     * Test for {@link Discovery#getMaxDBLinux()}.
     */
    @Test
    public void getXServerLinux() {
        String actual;
        Discovery discovery;
        try {
            discovery = new Discovery();
        } catch (Exception e) {
            Assert.fail("Unexpected critical exception occurred : " + e.getMessage());
            return;
        }
        try {
            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_typical")), null, Discovery.MODE_NAMES_NONE);
            actual = discovery.getXServerLinux();
            assertEquals("{\"data\":[{\"{#XSERVERNAME}\":\"XServer-SDB-7200\", \"{#XSERVERPORT}\":\"7200\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-SDB-NI-7201\", \"{#XSERVERPORT}\":\"7201\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-SDB-SSL-7202\", \"{#XSERVERPORT}\":\"7202\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-CDB-7203\", \"{#XSERVERPORT}\":\"7203\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-CDB-NI-7204\", \"{#XSERVERPORT}\":\"7204\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-CDB-SSL-7205\", \"{#XSERVERPORT}\":\"7205\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_typical")), "CDB", Discovery.MODE_NAMES_EXCLUDE);
            actual = discovery.getXServerLinux();
            assertEquals("{\"data\":[{\"{#XSERVERNAME}\":\"XServer-SDB-7200\", \"{#XSERVERPORT}\":\"7200\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-SDB-NI-7201\", \"{#XSERVERPORT}\":\"7201\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-SDB-SSL-7202\", \"{#XSERVERPORT}\":\"7202\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_typical")), "SDB,CDB", Discovery.MODE_NAMES_EXCLUDE);
            actual = discovery.getXServerLinux();
            assertEquals("{\"data\":[]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_typical")), "CDB", Discovery.MODE_NAMES_ONLY);
            actual = discovery.getXServerLinux();
            assertEquals("{\"data\":[{\"{#XSERVERNAME}\":\"XServer-CDB-7203\", \"{#XSERVERPORT}\":\"7203\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-CDB-NI-7204\", \"{#XSERVERPORT}\":\"7204\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-CDB-SSL-7205\", \"{#XSERVERPORT}\":\"7205\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_typical")), "SDB,CDB", Discovery.MODE_NAMES_ONLY);
            actual = discovery.getXServerLinux();
            assertEquals("{\"data\":[{\"{#XSERVERNAME}\":\"XServer-SDB-7200\", \"{#XSERVERPORT}\":\"7200\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-SDB-NI-7201\", \"{#XSERVERPORT}\":\"7201\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-SDB-SSL-7202\", \"{#XSERVERPORT}\":\"7202\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-CDB-7203\", \"{#XSERVERPORT}\":\"7203\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-CDB-NI-7204\", \"{#XSERVERPORT}\":\"7204\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-CDB-SSL-7205\", \"{#XSERVERPORT}\":\"7205\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_typical")), "VDB,UDB", Discovery.MODE_NAMES_ONLY);
            actual = discovery.getXServerLinux();
            assertEquals("{\"data\":[]}", actual);
        } catch (DiscoveryException e) {
            Assert.fail("DiscoveryException : " + e.getMessage());
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        }

        try {
            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/IniFile_params_missing")), null, Discovery.MODE_NAMES_NONE);
            actual = discovery.getXServerLinux();
            Assert.fail(String.format("Missing DiscoveryException, result : <%s>.", actual));
        } catch (DiscoveryException e) {
            //ok
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        }
    }

    /**
     * Test for {@link Discovery#getXServerWindows()}.
     */
    @Test
    public void getXServerWindows() {
        String actual;
        Discovery discovery;
        try {
            discovery = new Discovery();
        } catch (Exception e) {
            Assert.fail("Unexpected critical exception occurred : " + e.getMessage());
            return;
        }
        try {
            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_xserver_typical")), null, Discovery.MODE_NAMES_NONE);
            actual = discovery.getXServerWindows();
            assertEquals("{\"data\":[{\"{#XSERVERNAME}\":\"XServer-SDB-7201\", \"{#XSERVERPORT}\":\"7201\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-CDB-7203\", \"{#XSERVERPORT}\":\"7203\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_xserver_typical")), "SDB", Discovery.MODE_NAMES_EXCLUDE);
            actual = discovery.getXServerWindows();
            assertEquals("{\"data\":[{\"{#XSERVERNAME}\":\"XServer-CDB-7203\", \"{#XSERVERPORT}\":\"7203\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_xserver_typical")), "SDB,CDB", Discovery.MODE_NAMES_EXCLUDE);
            actual = discovery.getXServerWindows();
            assertEquals("{\"data\":[]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_xserver_typical")), "SDB", Discovery.MODE_NAMES_ONLY);
            actual = discovery.getXServerWindows();
            assertEquals("{\"data\":[{\"{#XSERVERNAME}\":\"XServer-SDB-7201\", \"{#XSERVERPORT}\":\"7201\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_xserver_typical")), "SDB,CDB", Discovery.MODE_NAMES_ONLY);
            actual = discovery.getXServerWindows();
            assertEquals("{\"data\":[{\"{#XSERVERNAME}\":\"XServer-SDB-7201\", \"{#XSERVERPORT}\":\"7201\"}, " +
                    "{\"{#XSERVERNAME}\":\"XServer-CDB-7203\", \"{#XSERVERPORT}\":\"7203\"}]}", actual);

            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_xserver_typical")), "DDB", Discovery.MODE_NAMES_ONLY);
            actual = discovery.getXServerWindows();
            assertEquals("{\"data\":[]}", actual);
        } catch (DiscoveryException e) {
            e.printStackTrace();
            Assert.fail("DiscoveryException : " + e.getMessage());
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        }

        try {
            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_xserver_wrong_path")), null, Discovery.MODE_NAMES_NONE);
            actual = discovery.getXServerWindows();
            assertEquals("{\"data\":[]}", actual);
        } catch (DiscoveryException e) {
            Assert.fail("DiscoveryException : " + e.getMessage());
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        }

        try {
            discovery.init(TestFileReader.readFile(DiscoveryTest.class.getResourceAsStream("/discovery/Reg_xserver_missing")), null, Discovery.MODE_NAMES_NONE);
            actual = discovery.getXServerWindows();
            assertEquals("{\"data\":[]}", actual);
        } catch (DiscoveryException e) {
            Assert.fail("DiscoveryException : " + e.getMessage());
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        }
    }
}
