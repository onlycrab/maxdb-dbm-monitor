package com.github.onlycrab.maxdbmon.discovery.linux;

import com.github.onlycrab.maxdbmon.discovery.DiscoveryException;
import com.github.onlycrab.util.TestFileReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Test class for {@link InstParser}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class InstParserTest {
    /**
     * Test for {@link InstParser#parse(int)}.
     */
    @Test
    public void parse() {
        List<LinuxDiscoveryData> dbs;
        HashMap<String, LinuxDiscoveryData> dbMap = new HashMap<>();
        InstParser parser;

        //Typical
        try {
            parser = new InstParser(TestFileReader.readFile(InstParserTest.class.getResourceAsStream("/discovery/IniFile_typical")));
            dbs = parser.parse(InstParser.XSERVER_DISCOVERY_MODE);
            for (LinuxDiscoveryData db : dbs) {
                dbMap.put(db.getName(), db);
            }
            Assert.assertEquals("NAME=<SDB>, VERSION=<7.9.7.10>, PORT=<7200>, NIPORT=<7201>, SSLPORT=<7202>",
                    dbMap.get("SDB").toString());
            Assert.assertEquals("NAME=<CDB>, VERSION=<7.8.7.10>, PORT=<7203>, NIPORT=<7204>, SSLPORT=<7205>",
                    dbMap.get("CDB").toString());

            dbs = parser.parse(InstParser.DB_DISCOVERY_MODE);
            dbMap.clear();
            for (LinuxDiscoveryData db : dbs) {
                dbMap.put(db.getName(), db);
            }
            Assert.assertEquals("NAME=<SDB>, VERSION=<7.9.7.10>, PORT=<0>, NIPORT=<0>, SSLPORT=<0>",
                    dbMap.get("SDB").toString());
            Assert.assertEquals("NAME=<CDB>, VERSION=<7.8.7.10>, PORT=<0>, NIPORT=<0>, SSLPORT=<0>",
                    dbMap.get("CDB").toString());
        } catch (DiscoveryException e) {
            e.printStackTrace();
            Assert.fail("Parse error : " + e.getMessage());
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        } catch (NullPointerException e) {
            Assert.fail("NullPointerException : cant find one of DBs by name.");
        }

        //Installations missing
        try {
            parser = new InstParser(TestFileReader.readFile(InstParserTest.class.getResourceAsStream("/discovery/IniFile_installations_missing")));
            dbs = parser.parse(InstParser.XSERVER_DISCOVERY_MODE);
            if (dbs.size() != 0) {
                StringBuilder msg = new StringBuilder("DB list must be empty. Founded :");
                for (LinuxDiscoveryData db : dbs) {
                    msg.append("\n").append(db);
                }
                Assert.fail(msg.toString());
            }
        } catch (DiscoveryException ignored) {
            //ok
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        }

        //Params missing
        try {
            parser = new InstParser(TestFileReader.readFile(InstParserTest.class.getResourceAsStream("/discovery/IniFile_params_missing")));
            parser.parse(InstParser.XSERVER_DISCOVERY_MODE);
            Assert.fail("Wrong file format, missing ParseException.");
        } catch (DiscoveryException e) {
            if (!e.getMessage().contains("occurs 2 times in one section")) {
                Assert.fail("Parse error : " + e.getMessage());
            }
        } catch (IOException e) {
            Assert.fail("IOException : " + e.getMessage());
        }
    }
}
