package com.github.onlycrab.maxdbmon.xserver;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static junit.framework.TestCase.assertEquals;

/**
 * Test class for {@link XServerChecker}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class XServerCheckerTest {
    private static final int startWith = 10000;
    private static final int endWith = 60000;

    private static ServerSocket create() throws Exception {
        int port = startWith;
        ServerSocket socket = null;
        while (socket == null) {
            try {
                socket = new ServerSocket(port);
            } catch (IOException ignore) {
                port++;
            }
            if (port > endWith) {
                throw new Exception(String.format("Cant open check socket : port from <%s> to <%s> are busy.", startWith, endWith));
            }
        }
        return socket;
    }

    private static void closeSocket(ServerSocket socket) {
        try {
            socket.close();
        } catch (IOException ignore) {
        }
    }

    /**
     * Test for {@link XServerChecker#check(String, String)}.
     */
    @Test
    public void check() {
        ServerSocket serverSocket = null;
        int localPort = -1;
        try {
            serverSocket = create();
            localPort = serverSocket.getLocalPort();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        try {
            assertEquals(1, XServerChecker.check("localhost", String.valueOf(localPort)));
        } catch (CheckSocketException e) {
            Assert.fail(e.getMessage());
        } finally {
            closeSocket(serverSocket);
        }

        try {
            assertEquals(0, XServerChecker.check("localhost", String.valueOf(localPort)));
        } catch (CheckSocketException e) {
            Assert.fail(e.getMessage());
        } finally {
            closeSocket(serverSocket);
        }

        try {
            XServerChecker.check("wrong-host-value", "10000");
        } catch (CheckSocketException e) {
            if (!e.getMessage().contains("determination error")) {
                Assert.fail("Wrong exception : " + e.getMessage());
                e.printStackTrace();
            }
        }

        try {
            XServerChecker.check("localhost", "999999");
        } catch (CheckSocketException e) {
            if (!e.getMessage().contains("Illegal port value")) {
                Assert.fail("Wrong exception : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
