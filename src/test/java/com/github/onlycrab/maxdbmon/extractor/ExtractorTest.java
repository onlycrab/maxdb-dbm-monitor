package com.github.onlycrab.maxdbmon.extractor;

import com.github.onlycrab.maxdbmon.extractor.comparator.ParamsComparator;
import com.github.onlycrab.maxdbmon.extractor.connector.DBMCommand;
import com.github.onlycrab.maxdbmon.extractor.connector.DBMConnector;
import com.github.onlycrab.util.AssertJSON;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test class for {@link Extractor}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class ExtractorTest extends Extractor{
    /**
     * Test for {@link Extractor#getOperStateRawJSON(String)}.
     */
    @Test
    public void getOperStateRawJSON(){
        Extractor extractor = new Extractor();
        String res;

        DBMConnector connector = Mockito.mock(DBMConnector.class);
        extractor.setConnector(connector);
        try {
            Mockito.when(connector.execute(DBMCommand.OPERATIONAL_STATE)).thenReturn("State\nONLINE");
            res = extractor.getOperStateRawJSON("mac1=v1#mac2=val2");
            AssertJSON.assertContains(res, "operstate", "1");
            AssertJSON.assertContains(res, "error", "");
            AssertJSON.assertContains(res, "zm_mac1", "v1");
            AssertJSON.assertContains(res, "zm_mac2", "val2");
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred : " + e.getMessage());
        }
    }

    /**
     * Test for {@link Extractor#getStateRawJSON(String)}.
     */
    @Test
    public void getStateRawJSON(){
        Extractor extractor = new Extractor();
        String res;

        DBMConnector connector = Mockito.mock(DBMConnector.class);
        extractor.setConnector(connector);
        try {
            Mockito.when(connector.execute(DBMCommand.OPERATIONAL_STATE)).thenReturn("State\nONLINE");
            Mockito.when(connector.execute(DBMCommand.INFO_STATE)).thenReturn("Name                    | Value\n" +
                    "\n" +
                    "Data           (KB)     = 2960\n" +
                    "Perm Data      (KB)     = 2904\n");
            Mockito.when(connector.execute(DBMCommand.INFO_DATA)).thenReturn("Name                | Value\n" +
                    "\n" +
                    "Devspace Name       = D:\\sapdb\\AAA\\sapdata\\DISKD0001\n" +
                    "   Total Space (KB) = 51200000\n" +
                    "   Used Space (KB)  = 51199960\n" +
                    "   Used Space (%)   = 99\n" +
                    "   Free Space (KB)  = 40\n" +
                    "   Free Space (%)   = 1\n" +
                    "Devspace Name       = D:\\sapdb\\AAA\\sapdata\\DISKD0002\n" +
                    "   Total Space (KB) = 100000000\n" +
                    "   Used Space (KB)  = 99997208\n" +
                    "   Used Space (%)   = 99\n" +
                    "   Free Space (KB)  = 2792\n" +
                    "   Free Space (%)   = 1\n" +
                    "Devspace Name       = D:\\sapdb\\AAA\\sapdata\\DISKD0003\n" +
                    "   Total Space (KB) = 100000000\n" +
                    "   Used Space (KB)  = 99997152\n" +
                    "   Used Space (%)   = 99\n" +
                    "   Free Space (KB)  = 2848\n" +
                    "   Free Space (%)   = 1");
            res = extractor.getStateRawJSON("mac1=val1#mac2=");

            AssertJSON.assertContains(res, "data_bytes", "3031040");
            AssertJSON.assertContains(res, "perm_data_bytes", "2973696");
            AssertJSON.assertContains(res, "data_volumes_count", "3");
            AssertJSON.assertContains(res, "error", "");
            AssertJSON.assertContains(res, "zm_mac1", "val1");
            AssertJSON.assertContains(res, "zm_mac2", "");
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred : " + e.getMessage());
        }
    }

    /**
     * Test for {@link Extractor#getCacheHitRateRawJSON(String)}.
     */
    @Test
    public void getCacheRawJSON(){
        Extractor extractor = new Extractor();
        String res;

        DBMConnector connector = Mockito.mock(DBMConnector.class);
        extractor.setConnector(connector);
        try {
            Mockito.when(connector.execute(DBMCommand.OPERATIONAL_STATE)).thenReturn("State\nONLINE");
            Mockito.when(connector.execute(DBMCommand.INFO_CACHES)).thenReturn("Type                 | Accesses             | Successful           | Unsuccessful         | Hit Rate (%)\n" +
                    "\n" +
                    "DATA                 | 233915302            | 226345572            | 7569730              | 97          \n" +
                    "SEQUENCE             | 0                    | 0                    | 0                    | (null)      \n" +
                    "COMMANDPREPARE       | 430097               | 424703               | 10784                | 8           \n");
            res = extractor.getCacheHitRateRawJSON(null);

            AssertJSON.assertContains(res, "cache_hitrate_data", "97");
            AssertJSON.assertContains(res, "cache_hitrate_sequence", "0");
            AssertJSON.assertContains(res, "cache_hitrate_commandprepare", "8");
            AssertJSON.assertContains(res, "error", "");
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred : " + e.getMessage());
        }
    }

    /**
     * Test for {@link Extractor#getLogRawJSON(String)}.
     */
    @Test
    public void getLogRawJSON(){
        Extractor extractor = new Extractor();
        String res;

        DBMConnector connector = Mockito.mock(DBMConnector.class);
        extractor.setConnector(connector);
        try {
            Mockito.when(connector.execute(DBMCommand.OPERATIONAL_STATE)).thenReturn("State\nONLINE");
            Mockito.when(connector.execute(DBMCommand.INFO_LOG)).thenReturn("Log Mirrored                    = NO\n" +
                    "Log Writing                     = ON\n" +
                    "Log Automatic Overwrite         = OFF\n" +
                    "Max. Size (KB)                  = 11249328\n" +
                    "Backup Segment Size (KB)        = 341328\n" +
                    "Backup Interval (seconds)       = 7200");
            res = extractor.getLogRawJSON(null);

            AssertJSON.assertContains(res, "log_mirrored", "0");
            AssertJSON.assertContains(res, "log_writing", "1");
            AssertJSON.assertContains(res, "log_automatic_overwrite", "0");
            AssertJSON.assertContains(res, "log_max_size_bytes", "11519311872");
            AssertJSON.assertContains(res, "error", "");
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred : " + e.getMessage());
        }
    }

    /**
     * Test for {@link Extractor#getLocksRawJSON(String)}.
     */
    @Test
    public void getLocksRawJSON(){
        Extractor extractor = new Extractor();
        String res;

        DBMConnector connector = Mockito.mock(DBMConnector.class);
        extractor.setConnector(connector);
        try {
            Mockito.when(connector.execute(DBMCommand.OPERATIONAL_STATE)).thenReturn("State\nONLINE");
            Mockito.when(connector.execute(DBMCommand.INFO_LOCKS)).thenReturn("Name                 | Value\n" +
                    "\n" +
                    "Max. Entries         = 3602400\n" +
                    "Average Used Entries = 4\n" +
                    "Collisions           = 3\n" +
                    "Escalations          = 11\n" +
                    "Row Locks            = 297260\n" +
                    "Table Locks          = 650431\n" +
                    "Request Timeout      = 3600");
            res = extractor.getLocksRawJSON(null);

            AssertJSON.assertContains(res, "locks_max_entries", "3602400");
            AssertJSON.assertContains(res, "locks_collisions", "3");
            AssertJSON.assertContains(res, "locks_escalations", "11");
            AssertJSON.assertContains(res, "locks_row_locks", "297260");
            AssertJSON.assertContains(res, "locks_table_locks", "650431");
            AssertJSON.assertContains(res, "error", "");
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred : " + e.getMessage());
        }
    }

    /**
     * Test for {@link Extractor#getBackupRawJSON(String)}.
     */
    @Test
    public void getBackupRawJSON(){
        Extractor extractor = new Extractor();
        String res;

        DBMConnector connector = Mockito.mock(DBMConnector.class);
        extractor.setConnector(connector);
        try {
            Mockito.when(connector.execute(DBMCommand.BACKUP_HISTORY)).thenReturn("LOG_000000706|2021-10-01 12:09:51|2021-10-01 12:09:51|         0|                               some text|");
            res = extractor.getBackupRawJSON("mac1=val1");
            if (!res.contains("LOG_SUCCESSFUL\":\"1\"") || !res.contains("\"error\":\"\"") || !res.contains("\"zm_mac1\":\"val1\"")){
                System.err.println(res);
                Assert.fail("Wrong backup data format");
            }
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred : " + e.getMessage());
        }
    }

    /**
     * Test for {@link Extractor#getParamsDiffRawJSON(String)}.
     */
    @Test
    public void getParamsDiffRawJSON(){
        Extractor extractor = new Extractor();
        String res;

        DBMConnector connector = Mockito.mock(DBMConnector.class);
        extractor.setConnector(connector);
        ParamsComparator comparator = Mockito.mock(ParamsComparator.class);
        extractor.paramsComparator = comparator;
        try {
            String testRes = "MaxLogWriterTasks                | 0\n" +
                    "_MAX_BACKUP_TASKS                | 0";
            Mockito.when(connector.execute(DBMCommand.OPERATIONAL_STATE)).thenReturn("State\nONLINE");
            Mockito.when(connector.execute(DBMCommand.INFO_PARAMS)).thenReturn(testRes);
            Mockito.doNothing().when(comparator).init(null, testRes);
            Mockito.when(comparator.getDifference()).thenCallRealMethod();
            res = extractor.getParamsDiffRawJSON(null);

            AssertJSON.assertContains(res, "diff", "");
            AssertJSON.assertContains(res, "error", "");
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred : " + e.getMessage());
        }
    }
}
