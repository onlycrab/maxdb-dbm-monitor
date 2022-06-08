package com.github.onlycrab.maxdbmon.discovery.linux;

/**
 * Object for storing backup of the MaxDB and XServer installation (for OS linux).
 *
 * @author Roman Ryncovich
 * @version 0.9
 */
public class LinuxDiscoveryData {
    /**
     * MaxDB instance name.
     */
    private final String name;
    /**
     * MaxDB instance version.
     */
    private final String version;

    /**
     * XServer port.
     */
    private int port;

    /**
     * XServerNi port.
     */
    private int niPort;

    /**
     * XServerSsl port.
     */
    private int sslPort;

    /**
     * Create new instance.
     *
     * @param name    MaxDB name
     * @param version MaxDB version
     */
    LinuxDiscoveryData(String name, String version) {
        if (name != null) {
            this.name = name;
        } else {
            this.name = "";
        }
        if (version != null) {
            this.version = version;
        } else {
            this.version = "";
        }
        port = 0;
        niPort = 0;
        sslPort = 0;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public String getVersion() {
        return version;
    }

    public int getPort() {
        return port;
    }

    void setPort(int port) {
        this.port = port;
    }

    public int getNiPort() {
        return niPort;
    }

    void setNiPort(int niPort) {
        this.niPort = niPort;
    }

    public int getSslPort() {
        return sslPort;
    }

    void setSslPort(int sslPort) {
        this.sslPort = sslPort;
    }

    @Override
    public String toString() {
        return String.format("NAME=<%s>, VERSION=<%s>, PORT=<%s>, NIPORT=<%s>, SSLPORT=<%s>", name, version, port, niPort, sslPort);
    }
}
