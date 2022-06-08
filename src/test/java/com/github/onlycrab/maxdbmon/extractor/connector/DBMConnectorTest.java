package com.github.onlycrab.maxdbmon.extractor.connector;

import com.sap.dbtech.powertoys.DBM;
import com.sap.dbtech.powertoys.DBMException;
import com.sap.dbtech.rte.comm.RTEException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test class for {@link DBMConnector}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class DBMConnectorTest extends DBMConnector {
    /**
     * Test for {@link DBMConnector#removeDuplicateLineBreak(String)}.
     */
    @Test
    public void removeDuplicateLineBreak(){
        Assert.assertNull(removeDuplicateLineBreak(null));
        Assert.assertEquals("", removeDuplicateLineBreak(""));
        Assert.assertEquals("\n", removeDuplicateLineBreak("\n"));
        Assert.assertEquals("", removeDuplicateLineBreak("\n\n"));
        Assert.assertEquals("asd\n", removeDuplicateLineBreak("asd\n\n"));
        Assert.assertEquals("asd\n", removeDuplicateLineBreak("asd"));
    }

    /**
     * Test for {@link DBMConnector#execute(DBMCommand)}.
     */
    @Test
    public void executeOnly(){
        DBMConnector connector = new DBMConnector();

        DBM session = Mockito.mock(DBM.class);
        connector.session = session;

        //emulate paging in command result
        DBMCommand command = DBMCommand.INFO_STATE;
        final int[] callCount = {0};
        final String[] ans = new String[]{
                "CONTINUE    \nsome val2 \nsome val3\n",
                "CONTINUE    \nsome val4 \nsome val5\n\n\n",
                "CONTINUE    \nsome val6  ",
                "END    \nsome val7      \n\n"
        };
        try {
            Mockito.when(session.cmd(command.getCommandText())).thenReturn("CONTINUE    \nsome val\n\n");
            Mockito.when(session.cmd(command.getCommandTextNext())).thenAnswer(invocation -> {
                callCount[0]++;
                if (callCount[0] <= ans.length){
                    return ans[callCount[0] - 1];
                } else {
                    return "";
                }
            });
        } catch (DBMException | RTEException e) {
            Assert.fail("Unexpected exception : " + e.getMessage());
            return;
        }

        try {
            String result = connector.executeOnly(command);
            Assert.assertEquals("some val\n" +
                    "some val2 \n" +
                    "some val3\n" +
                    "some val4 \n" +
                    "some val5\n" +
                    "some val6  \n" +
                    "some val7      \n", result);
        } catch (DBMConnectionException e) {
            Assert.fail("Unexpected exception : " + e.getMessage());
        }
    }
}
