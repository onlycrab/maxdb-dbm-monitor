package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.maxdbmon.extractor.backup.BackupArgument;
import com.github.onlycrab.maxdbmon.extractor.backup.BackupData;
import com.github.onlycrab.maxdbmon.extractor.backup.BackupSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test class for {@link BackupHistoryParser}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class BackupHistoryParserTest {
    private final String dataTestString = "LOG_000000706|2021-10-01 12:09:51|2021-10-01 12:09:51|         0|                               some text|\n" +
            "LOG_000000705|2021-10-01 10:09:51|2021-10-01 10:09:51|         0|                                        |\n" +
            "DAT_000003084|2021-09-30 21:01:22|2021-10-01 07:15:37|        22|                                        |\n" +
            "PAG_000000704|2021-09-30 21:01:21|2021-09-30 21:01:35|        -3|                                     err|\n" +
            "DAT_000003082|2021-09-28 21:01:22|2021-09-29 07:49:40|         0|                                     msg|";

    /**
     * Test for {@link BackupHistoryParser#getDataMap(String)}.
     */
    @Test
    public void getDataMap() {
        BackupHistoryParser backupHistoryParser = new BackupHistoryParser();
        Map<String, String> map = backupHistoryParser.getDataMap(dataTestString);
        assertEquals("706", map.get(BackupSource.LOG + "_" + BackupArgument.NUMBER.toString()));
        assertEquals("0", map.get(BackupSource.LOG + "_" + BackupArgument.DURATION.toString()));
        assertEquals("1", map.get(BackupSource.LOG + "_" + BackupArgument.SUCCESSFUL.toString()));
        assertEquals("20211001", map.get(BackupSource.LOG + "_" + BackupArgument.START_DATE.toString()));
        assertEquals("120951", map.get(BackupSource.LOG + "_" + BackupArgument.START_TIME.toString()));
        assertEquals("20211001", map.get(BackupSource.LOG + "_" + BackupArgument.END_DATE.toString()));
        assertEquals("120951", map.get(BackupSource.LOG + "_" + BackupArgument.END_TIME.toString()));
        assertEquals("some text", map.get(BackupSource.LOG + "_" + BackupArgument.MESSAGE.toString()));

        assertEquals("3084", map.get(BackupSource.DAT + "_" + BackupArgument.NUMBER.toString()));
        assertEquals("36855", map.get(BackupSource.DAT + "_" + BackupArgument.DURATION.toString()));
        assertEquals("0", map.get(BackupSource.DAT + "_" + BackupArgument.SUCCESSFUL.toString()));
        assertEquals("20210930", map.get(BackupSource.DAT + "_" + BackupArgument.START_DATE.toString()));
        assertEquals("210122", map.get(BackupSource.DAT + "_" + BackupArgument.START_TIME.toString()));
        assertEquals("20211001", map.get(BackupSource.DAT + "_" + BackupArgument.END_DATE.toString()));
        assertEquals("071537", map.get(BackupSource.DAT + "_" + BackupArgument.END_TIME.toString()));
        assertEquals("Code <22> : <>", map.get(BackupSource.DAT + "_" + BackupArgument.MESSAGE.toString()));

        assertEquals("704", map.get(BackupSource.PAG + "_" + BackupArgument.NUMBER.toString()));
        assertEquals("14", map.get(BackupSource.PAG + "_" + BackupArgument.DURATION.toString()));
        assertEquals("0", map.get(BackupSource.PAG + "_" + BackupArgument.SUCCESSFUL.toString()));
        assertEquals("20210930", map.get(BackupSource.PAG + "_" + BackupArgument.START_DATE.toString()));
        assertEquals("210121", map.get(BackupSource.PAG + "_" + BackupArgument.START_TIME.toString()));
        assertEquals("20210930", map.get(BackupSource.PAG + "_" + BackupArgument.END_DATE.toString()));
        assertEquals("210135", map.get(BackupSource.PAG + "_" + BackupArgument.END_TIME.toString()));
        assertEquals("Code <-3> : <err>", map.get(BackupSource.PAG + "_" + BackupArgument.MESSAGE.toString()));
    }

    /**
     * Test for {@link BackupHistoryParser#parseLine(String)}.
     */
    @Test
    public void parseLine() {
        String[][] data = {
                {"LOG_000000000|2021-02-10 13:40:58|2021-02-10 13:40:58|      -903|Host file I/O error                     |",
                        "Source : <LOG>, number : <0>, start at : <2021-02-10 13:40:58>, end at : <2021-02-10 13:40:58>, code : <-903>, msg : <Host file I/O error>"},
                {"DAT_000002882|2021-02-18 18:01:43|2021-02-19 00:37:20|         0|                                        |",
                        "Source : <DAT>, number : <2882>, start at : <2021-02-18 18:01:43>, end at : <2021-02-19 00:37:20>, code : <0>, msg : <>"},
                {"PAG_000000000|||      -903|Host file I/O error                     |",
                        "Source : <PAG>, number : <0>, start at : <2000-01-01 00:00:00>, end at : <2000-01-01 00:00:00>, code : <-903>, msg : <Host file I/O error>"}
        };
        String[] dataNull = {null, "", "LOG_000000000|2021-02-10 13:40:58|2021-02-10 13:40:58|"};

        for (String[] test : data) {
            try {
                assertEquals(test[1], Objects.requireNonNull(BackupHistoryParser.parseLine(test[0])).toString());
            } catch (NullPointerException e) {
                Assert.fail("NullPointerException at parse line <" + test[1] + ">.");
            }
        }
        for (String test : dataNull) {
            assertNull(BackupHistoryParser.parseLine(test));
        }
    }

    /**
     * Test for {@link BackupHistoryParser#parse(String)}.
     */
    @Test
    public void parse() {
        ArrayList<BackupData> expected = new ArrayList<>();
        expected.add(new BackupData(BackupSource.LOG, 706, "2021-10-01 12:09:51", "2021-10-01 12:09:51", 0, "some text"));
        expected.add(new BackupData(BackupSource.LOG, 705, "2021-10-01 10:09:51", "2021-10-01 10:09:51", 0, ""));
        expected.add(new BackupData(BackupSource.DAT, 3084, "2021-09-30 21:01:22", "2021-10-01 07:15:37", 22, ""));
        expected.add(new BackupData(BackupSource.PAG, 704, "2021-09-30 21:01:21", "2021-09-30 21:01:35", -3, "err"));
        expected.add(new BackupData(BackupSource.DAT, 3082, "2021-09-28 21:01:22", "2021-09-29 07:49:40", 0, "msg"));

        ArrayList<BackupData> actual = BackupHistoryParser.parse(dataTestString);
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }
}
