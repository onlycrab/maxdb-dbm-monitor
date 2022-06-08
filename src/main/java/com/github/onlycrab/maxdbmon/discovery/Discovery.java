package com.github.onlycrab.maxdbmon.discovery;

import com.github.onlycrab.common.OS;
import com.github.onlycrab.common.UtilitiesString;
import com.github.onlycrab.maxdbmon.discovery.linux.InstParser;
import com.github.onlycrab.maxdbmon.discovery.linux.LinuxDiscoveryData;
import com.github.onlycrab.maxdbmon.discovery.windows.RegParser;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class for discovery MaxDB and XServer instances.
 * <p>
 * Discovery is available in 3 modes:
 * 1.{@link Discovery#MODE_NAMES_NONE} - returns all discovered objects;
 * 2.{@link Discovery#MODE_NAMES_ONLY} - returns only objects, that are <strong>IN</strong> the names list;
 * 3.{@link Discovery#MODE_NAMES_EXCLUDE} - returns only objects, that are <strong>NOT IN</strong> the names list.
 * </p>
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class Discovery {
    /**
     * Ignore names list, discover all objects.
     */
    public static final int MODE_NAMES_NONE = 0;

    /**
     * Discover only objects, that are in the names list.
     */
    public static final int MODE_NAMES_ONLY = 1;

    /**
     * Discover only objects, that are not in the names list.
     */
    public static final int MODE_NAMES_EXCLUDE = 2;

    /**
     * OS type.
     */
    private final OS.OSType osType;

    /**
     * URI of {@code Installations.ini} for Linux systems.
     */
    private String path;

    /**
     * List of backup strings (not parsed, like in {@code Installations.ini} file).
     */
    private List<String> data;

    /**
     * List of database or XServer names.
     */
    private List<String> names;

    /**
     * Database or XServer names preprocessing mode.
     */
    private int mode;

    /**
     * Create new discovery object.
     *
     * @throws DiscoveryException if system OS type is unknown
     */
    public Discovery() throws DiscoveryException {
        osType = OS.getOSType(true);
        if (osType.equals(OS.OSType.UNKNOWN)) {
            throw new DiscoveryException("Unknown OS.");
        }
        data = null;
        path = null;
        mode = MODE_NAMES_NONE;
        names = new ArrayList<>();
    }

    /**
     * Parse names list to {@link ArrayList}.
     *
     * @param nameList list of names
     * @param mode     names mode
     */
    private void parseNames(String nameList, int mode) {
        if (mode == MODE_NAMES_ONLY || mode == MODE_NAMES_EXCLUDE) {
            this.mode = mode;
            names = UtilitiesString.parseToList(nameList, ",");
        } else {
            this.mode = MODE_NAMES_NONE;
            names = new ArrayList<>();
        }
    }

    /**
     * Initialize discovery object.
     *
     * @param path     URI of {@code Installations.ini} (only for Linux systems)
     * @param nameList list of names
     * @param mode     names mod
     */
    public void init(@Nullable String path, @Nullable String nameList, int mode) {
        this.path = path;
        parseNames(nameList, mode);
        data = null;
    }

    /**
     * Initialize discovery object.
     *
     * @param data     list of backup strings (not parsed, like in {@code Installations.ini} file)
     * @param nameList list of names
     * @param mode     names mod
     */
    public void init(@Nullable List<String> data, @Nullable String nameList, int mode) {
        this.path = null;
        parseNames(nameList, mode);
        this.data = data;
    }

    /**
     * Initialize discovery object.
     *
     * @param nameList list of names
     * @param mode     names mod
     */
    public void init(@Nullable String nameList, int mode) {
        this.path = null;
        parseNames(nameList, mode);
        this.data = null;
    }

    /**
     * Returns JSON of MaxDB instances.
     * <p>This is universal method: can be called from Windows or Linux systems.</p>
     *
     * @return JSON of MaxDB instances
     * @throws DiscoveryException if system OS type is unknown
     */
    public String getMaxDB() throws DiscoveryException {
        if (osType.equals(OS.OSType.WINDOWS)) {
            return getMaxDBWindows();
        } else {
            return getMaxDBLinux();
        }
    }

    /**
     * Returns JSON of XServer instances.
     * <p>This is universal method: can be called from Windows or Linux systems.</p>
     *
     * @return JSON of XServer instances
     * @throws DiscoveryException if system OS type is unknown
     */
    public String getXServer() throws DiscoveryException {
        if (osType.equals(OS.OSType.WINDOWS)) {
            return getXServerWindows();
        } else {
            return getXServerLinux();
        }
    }

    /**
     * Create instance of {@link InstParser}.
     *
     * @return instance of {@link InstParser}
     */
    private InstParser getLinuxIniParser() {
        if (path != null) {
            return new InstParser(path);
        } else if (data != null) {
            return new InstParser(data);
        } else {
            return new InstParser();
        }
    }

    /**
     * Returns JSON of MaxDB instances for Linux systems.
     *
     * @return JSON of MaxDB instances
     * @throws DiscoveryException if {@code Installation.ini} read error occurred;
     *                            if {@code Installation.ini} is in the wrong format
     */
    public String getMaxDBLinux() throws DiscoveryException {
        StringBuilder sb = new StringBuilder("{\"data\":[");
        InstParser parser = getLinuxIniParser();
        boolean addComma = false;
        for (LinuxDiscoveryData db : parser.parse(InstParser.DB_DISCOVERY_MODE)) {
            if ((mode == MODE_NAMES_EXCLUDE && !names.contains(db.getName())) ||
                    (mode == MODE_NAMES_ONLY && names.contains(db.getName())) ||
                    (mode != MODE_NAMES_EXCLUDE && mode != MODE_NAMES_ONLY)) {
                if (addComma) {
                    sb.append(", ");
                } else {
                    addComma = true;
                }
                sb.append(String.format("{\"{#MAXDBNAME}\":\"%s\"}", db.getName()));
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    /**
     * Returns JSON of MaxDB instances for Windows systems.
     *
     * @return JSON of MaxDB instances
     * @throws DiscoveryException if some error occurred while reading the Windows registry
     */
    public String getMaxDBWindows() throws DiscoveryException {
        StringBuilder sb = new StringBuilder("{\"data\":[");
        boolean addComma = false;
        for (String dbName : RegParser.getMaxDBList(data)) {
            if ((mode == MODE_NAMES_EXCLUDE && !names.contains(dbName)) ||
                    (mode == MODE_NAMES_ONLY && names.contains(dbName)) ||
                    (mode != MODE_NAMES_EXCLUDE && mode != MODE_NAMES_ONLY)) {
                if (addComma) {
                    sb.append(", ");
                } else {
                    addComma = true;
                }
                sb.append(String.format("{\"{#MAXDBNAME}\":\"%s\"}", dbName));
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    /**
     * Returns JSON of XServer instances for Linux systems.
     *
     * @return JSON of XServer instances
     * @throws DiscoveryException if {@code Installation.ini} read error occurred;
     *                            if {@code Installation.ini} is in the wrong format
     */
    public String getXServerLinux() throws DiscoveryException {
        StringBuilder sb = new StringBuilder("{\"data\":[");
        InstParser parser = getLinuxIniParser();
        boolean addComma = false;
        for (LinuxDiscoveryData db : parser.parse(InstParser.XSERVER_DISCOVERY_MODE)) {
            if ((mode == MODE_NAMES_EXCLUDE && !names.contains(db.getName())) ||
                    (mode == MODE_NAMES_ONLY && names.contains(db.getName())) ||
                    (mode != MODE_NAMES_EXCLUDE && mode != MODE_NAMES_ONLY)) {
                if (db.getPort() > 0) {
                    if (addComma) {
                        sb.append(", ");
                    }
                    sb.append(String.format("{\"{#XSERVERNAME}\":\"%s\", \"{#XSERVERPORT}\":\"%s\"}",
                            "XServer-" + db.getName() + "-" + db.getPort(), db.getPort()));
                    addComma = true;
                }
                if (db.getNiPort() > 0) {
                    if (addComma) {
                        sb.append(", ");
                    }
                    sb.append(String.format("{\"{#XSERVERNAME}\":\"%s\", \"{#XSERVERPORT}\":\"%s\"}",
                            "XServer-" + db.getName() + "-NI-" + db.getNiPort(), db.getNiPort()));
                    addComma = true;
                }
                if (db.getNiPort() > 0) {
                    if (addComma) {
                        sb.append(", ");
                    }
                    sb.append(String.format("{\"{#XSERVERNAME}\":\"%s\", \"{#XSERVERPORT}\":\"%s\"}",
                            "XServer-" + db.getName() + "-SSL-" + db.getSslPort(), db.getSslPort()));
                    addComma = true;
                }
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    /**
     * Returns JSON of XServer instances for Windows systems.
     *
     * @return JSON of XServer instances
     * @throws DiscoveryException if some error occurred while reading the Windows registry
     */
    public String getXServerWindows() throws DiscoveryException {
        StringBuilder sb = new StringBuilder("{\"data\":[");
        boolean addComma = false;
        for (Map.Entry<String, Integer> entry : RegParser.getXServerMap(data).entrySet()) {
            if ((mode == MODE_NAMES_EXCLUDE && !names.contains(entry.getKey())) ||
                    (mode == MODE_NAMES_ONLY && names.contains(entry.getKey())) ||
                    (mode != MODE_NAMES_EXCLUDE && mode != MODE_NAMES_ONLY)) {
                if (entry.getValue() > 0) {
                    if (addComma) {
                        sb.append(", ");
                    } else {
                        addComma = true;
                    }
                    sb.append(String.format("{\"{#XSERVERNAME}\":\"%s\", \"{#XSERVERPORT}\":\"%s\"}",
                            "XServer-" + entry.getKey() + "-" + entry.getValue(), entry.getValue()));
                }
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
