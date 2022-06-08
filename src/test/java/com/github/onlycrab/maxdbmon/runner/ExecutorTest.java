package com.github.onlycrab.maxdbmon.runner;

import com.github.onlycrab.maxdbmon.discovery.Discovery;
import com.github.onlycrab.maxdbmon.discovery.DiscoveryException;
import com.github.onlycrab.maxdbmon.extractor.Extractor;
import com.github.onlycrab.maxdbmon.xserver.XServerChecker;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test class for {@link Executor}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class ExecutorTest extends Executor {
    public ExecutorTest() throws DiscoveryException {
        super();
    }

    /**
     * Test for {@link Executor#init(String[])}.
     */
    @Test
    public void initTest() {
        Executor executor;
        try {
            executor = new Executor();
        } catch (Exception e) {
            Assert.fail("Unexpected critical exception occurred : " + e.getMessage());
            return;
        }
        String tmp;
        try {
            //Initialization
            tmp = executor.init(null);

            //Help message
            checkHelp(tmp, "It is a utility");
            tmp = executor.init(new String[]{});
            checkHelp(tmp, "It is a utility");
            tmp = executor.init(new String[]{"-h"});
            checkHelp(tmp, "It is a utility");
            tmp = executor.init(new String[]{"--help"});
            checkHelp(tmp, "It is a utility");
            tmp = executor.init(new String[]{"--help", "node"});
            checkHelp(tmp, "Node on which the MaxDB instance is running");
            tmp = executor.init(new String[]{"-h", "d"});
            checkHelp(tmp, "MaxDB instance name");

            //Version info
            tmp = executor.init(new String[]{"-v"});
            checkHelp(tmp, TITLE);
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                Assert.fail("SAPDBC library is not loaded, check file <sapdbc.jar>");
            } else {
                Assert.fail("Unexpected exception occurred : " + e.getMessage());
            }
        }

        //Parse from external file
        try {
            executor.init(new String[]{"-ef", "some file path"});
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred : reading a non-existent file should not throw an error : " + e.getMessage());
        }

        //Require args
        try {
            executor.init(new String[]{"-ros"});
            Assert.fail("Exception expected, but no missing requirements");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ris"});
            Assert.fail("Exception expected, but no missing requirements");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ric"});
            Assert.fail("Exception expected, but no missing requirements");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rilg"});
            Assert.fail("Exception expected, but no missing requirements");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rilc"});
            Assert.fail("Exception expected, but no missing requirements");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rpd"});
            Assert.fail("Exception expected, but no missing requirements");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rb"});
            Assert.fail("Exception expected, but no missing requirements");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rxs"});
            Assert.fail("Exception expected, but no missing requirements");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-ris"});
            executor.init(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-ros"});
            executor.init(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-ric"});
            executor.init(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-rilg"});
            executor.init(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-rilc"});
            executor.init(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-rpd"});
            executor.init(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-rb"});
            executor.init(new String[]{"-n", "server", "-xp", "123", "-rxs"});
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred : " + e.getMessage());
        }

        //Conflict args
        try {
            executor.init(new String[]{"-ris", "-ros"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ris", "-ric"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ris", "-rilg"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ris", "-rilc"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ris", "-rpd"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ris", "-ros"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ris", "-rb"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ris", "-rxs"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ris", "-rdl"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ris", "-rxl"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }

        try {
            executor.init(new String[]{"-ros", "-rb"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ros", "-ric"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ros", "-rilg"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ros", "-rilc"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ros", "-rpd"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ros", "-rxs"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ros", "-rdl"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ros", "-rxl"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }

        try {
            executor.init(new String[]{"-rb", "-rxs"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rb", "-rdl"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rb", "-rxl"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rb", "-ric"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rb", "-rilg"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rb", "-rilc"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rb", "-rpd"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }

        try {
            executor.init(new String[]{"-rxs", "-rdl"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rxs", "-rxl"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rxs", "-ric"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rxs", "-rilg"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rxs", "-rilc"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rxs", "-rpd"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }

        try {
            executor.init(new String[]{"-rdl", "-rxl"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rdl", "-ric"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rdl", "-rilg"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rdl", "-rilc"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rdl", "-rpd"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }

        try {
            executor.init(new String[]{"-rxl", "-ric"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rxl", "-rilg"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rxl", "-rilc"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rxl", "-rpd"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }

        try {
            executor.init(new String[]{"-ric", "-rilg"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ric", "-rilc"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-ric", "-rpd"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }

        try {
            executor.init(new String[]{"-rilg", "-rilc"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
        try {
            executor.init(new String[]{"-rilg", "-rpd"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }

        try {
            executor.init(new String[]{"-rilc", "-rpd"});
            Assert.fail("Exception expected, but there is no conflict");
        } catch (Exception ignored) {
        }
    }

    private void checkHelp(String target, String expected) {
        if (target == null) {
            Assert.fail("Help message expected, but it is <null>");
        } else if (!target.contains(expected)) {
            Assert.fail("Help message not contains char sequence <" + expected + ">, message : " + target);
        }
    }

    /**
     * Test for {@link Executor#execute(String[])}.
     */
    @Test
    public void execute() {
        Executor executor;
        try {
            executor = new Executor();
        } catch (Exception e) {
            Assert.fail("Unexpected critical exception occurred : " + e.getMessage());
            return;
        }

        //Request missing
        if (!executor.execute(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass"}).
                startsWith("{\"error\":")) {
            Assert.fail("Unexpected error : no request, but result not contains error message");
        }

        //Get MaxDB data requests
        Extractor extractor = Mockito.mock(Extractor.class);
        executor.extractor = extractor;

        Mockito.when(extractor.getOperStateRawJSON("")).thenReturn("correct");
        executor.execute(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-ros"});
        verify(extractor, times(1)).getOperStateRawJSON("");

        Mockito.when(extractor.getStateRawJSON("")).thenReturn("correct");
        executor.execute(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-ris"});
        verify(extractor, times(1)).getStateRawJSON("");

        Mockito.when(extractor.getCacheHitRateRawJSON("")).thenReturn("correct");
        executor.execute(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-ric"});
        verify(extractor, times(1)).getCacheHitRateRawJSON("");

        Mockito.when(extractor.getLogRawJSON("")).thenReturn("correct");
        executor.execute(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-rilg"});
        verify(extractor, times(1)).getLogRawJSON("");

        Mockito.when(extractor.getLocksRawJSON("")).thenReturn("correct");
        executor.execute(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-rilc"});
        verify(extractor, times(1)).getLocksRawJSON("");

        Mockito.when(extractor.getBackupRawJSON("")).thenReturn("correct");
        executor.execute(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-rb"});
        verify(extractor, times(1)).getBackupRawJSON("");

        Mockito.when(extractor.getParamsDiffRawJSON("")).thenReturn("correct");
        executor.execute(new String[]{"-n", "server", "-d", "AAA", "-u", "user", "-p", "pass", "-rpd"});
        verify(extractor, times(1)).getParamsDiffRawJSON("");

        //Get XServer state request
        XServerChecker xServerChecker = Mockito.mock(XServerChecker.class);
        executor.xServerChecker = xServerChecker;
        try {
            Mockito.when(xServerChecker.getState()).thenReturn("1");
            executor.execute(new String[]{"-n", "server", "-xp", "123", "-rxs"});
            verify(xServerChecker, times(1)).getState();
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred : " + e.getMessage());
        }

        //Discovery
        Discovery discovery = Mockito.mock(Discovery.class);
        executor.discovery = discovery;
        try {
            Mockito.when(discovery.getMaxDB()).thenReturn("correct");
            executor.execute(new String[]{"-rdl"});
            verify(discovery, times(1)).getMaxDB();
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred : " + e.getMessage());
        }

        try {
            Mockito.when(discovery.getXServer()).thenReturn("correct");
            executor.execute(new String[]{"-rxl"});
            verify(discovery, times(1)).getXServer();
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred : " + e.getMessage());
        }

        //Discovery params
        //default
        executor.execute(new String[]{"-rdl"});
        Assert.assertNull(executor.dbNames);
        Assert.assertEquals(Discovery.MODE_NAMES_NONE, executor.discoveryMod);
        executor.execute(new String[]{"-rxl"});
        Assert.assertNull(executor.dbNames);
        Assert.assertEquals(Discovery.MODE_NAMES_NONE, executor.discoveryMod);
        //exclude
        executor.execute(new String[]{"-rdl", "-ed", "some"});
        Assert.assertEquals("some", executor.dbNames);
        Assert.assertEquals(Discovery.MODE_NAMES_EXCLUDE, executor.discoveryMod);
        executor.execute(new String[]{"-rxl", "-ed", "some"});
        Assert.assertEquals("some", executor.dbNames);
        Assert.assertEquals(Discovery.MODE_NAMES_EXCLUDE, executor.discoveryMod);
        //only
        executor.execute(new String[]{"-rdl", "-od", "AAA,BBB"});
        Assert.assertEquals("AAA,BBB", executor.dbNames);
        Assert.assertEquals(Discovery.MODE_NAMES_ONLY, executor.discoveryMod);
        executor.execute(new String[]{"-rxl", "-od", "AAA,BBB"});
        Assert.assertEquals("AAA,BBB", executor.dbNames);
        Assert.assertEquals(Discovery.MODE_NAMES_ONLY, executor.discoveryMod);
    }
}
