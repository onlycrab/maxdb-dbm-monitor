package com.github.onlycrab.maxdbmon.xserver;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Object is designed to check the connection with the XServer by creating a direct socket connection.
 *
 * @author Roman Ryncovich
 * @version 0.9
 */
@SuppressWarnings("WeakerAccess")
public class XServerChecker {
    /**
     * Return code which means that XServer is not responding on the tested port.
     */
    public static final int ERROR = 0;

    /**
     * Return code which means that XServer is responding on the tested port.
     */
    public static final int CONNECTED = 1;

    /**
     * Return code which means that the check does not need to be done.
     */
    public static final int SKIP = 2;

    /**
     * XServer host.
     */
    private String host;

    /**
     * XServer port.
     */
    private String port;

    /**
     * Create new instance.
     */
    public XServerChecker() {
        this.host = null;
        this.port = null;
    }

    /**
     * Returns XServer state code.
     *
     * @param host XServer host
     * @param port XServer port
     * @return XServer state code
     * @throws CheckSocketException if host or port values are illegal;
     *                              if unexpected {@link IOException} or {@link SecurityException} has occurred
     */
    public static int check(String host, String port) throws CheckSocketException {
        if (host == null) {
            throw new CheckSocketException("Host is <null>.");
        }
        if (host.trim().length() == 0) {
            throw new CheckSocketException("Host value is empty.");
        }
        if (port == null) {
            throw new CheckSocketException("Port is <null>.");
        }
        int portValue;
        try {
            portValue = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new CheckSocketException(String.format("Cant convert value <%s> to integer.", port));
        }
        if (portValue < 1) {
            return SKIP;
        }
        //noinspection EmptyTryBlock
        try (Socket socket = new Socket(host, portValue)) {
        } catch (ConnectException e) {
            return ERROR;
        } catch (UnknownHostException e) {
            throw new CheckSocketException(String.format("Host <%s> determination error : %s.", host, e.getMessage()),
                    e.getCause(), e.getStackTrace());
        } catch (IllegalArgumentException e) {
            throw new CheckSocketException(String.format("Illegal port value : %s.", e.getMessage()),
                    e.getCause(), e.getStackTrace());
        } catch (IOException | SecurityException e) {
            throw new CheckSocketException(String.format("Unexpected error : %s.", e.getMessage()), e.getCause(), e.getStackTrace());
        }
        return CONNECTED;
    }

    /**
     * Set XServer host.
     *
     * @param host value of XServer host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Set XServer port.
     *
     * @param port value of XServer port
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Returns XServer state code.
     *
     * @return XServer state code
     * @throws CheckSocketException if some error occurred while at {@link XServerChecker#check(String, String)}
     */
    public String getState() throws CheckSocketException {
        return String.format("{\"error\":\"\",\"xserver_state\":%s}", check(host, port));
    }
}
