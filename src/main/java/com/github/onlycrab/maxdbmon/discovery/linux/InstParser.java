package com.github.onlycrab.maxdbmon.discovery.linux;

import com.github.onlycrab.maxdbmon.discovery.DiscoveryException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for parsing MaxDB installation file (for OS linux).
 *
 * @author Roman Ryncovich
 * @version 0.9
 */
public class InstParser {
    /**
     * Mode for getting MaxDB instances backup.
     */
    public static final int DB_DISCOVERY_MODE = 0;
    /**
     * Mode for getting XServer instances backup.
     */
    public static final int XSERVER_DISCOVERY_MODE = 1;
    /**
     * Default path to MaxDB installation file {@code Installations.ini}.
     */
    private static final String DEFAULT_PATH = "/sapdb/data/config/Installations.ini";
    /**
     * Pattern for MaxDB instance path and version.
     */
    private static final String INST_PAIR_CHECK = "/sapdb/[A-Za-z]+/db=(\\d+\\.*)+";
    /**
     * Pattern for XServer section.
     */
    private static final String PARAMS_SEC_CHECK = "\\[Params-/sapdb/[A-Za-z]+/db]";
    /**
     * XServer port name.
     */
    private static final String PORT = "PortNoOrService";
    /**
     * XServerNi port name.
     */
    private static final String NI_PORT = "NIPortNoOrService";
    /**
     * XServerSsl port name.
     */
    private static final String SSL_PORT = "SSLPortNoOrService";
    /**
     * Path to MaxDB installation file {@code Installations.ini}.
     */
    private final String pathToIni;

    /**
     * MaxDB installation backup (list of string like in {@code Installations.ini} file).
     */
    private final List<String> data;

    /**
     * Create new instance with default {@code Installations.ini} path.
     */
    public InstParser() {
        this.pathToIni = DEFAULT_PATH;
        data = null;
    }

    /**
     * Create new instance.
     *
     * @param pathToIni path to {@code Installations.ini}
     */
    public InstParser(String pathToIni) {
        data = null;
        if (pathToIni != null) {
            if (pathToIni.trim().length() > 0) {
                this.pathToIni = pathToIni;
                return;
            }
        }
        this.pathToIni = DEFAULT_PATH;
    }

    /**
     * Create new instance with already received backup.
     *
     * @param data MaxDB installation backup : list of string like in {@code Installations.ini} file
     */
    public InstParser(List<String> data) {
        this.data = data;
        pathToIni = null;
    }

    /**
     * Returns list of strings from {@code pathToIni} file.
     *
     * @return list of strings from {@code pathToIni} file.
     * @throws DiscoveryException if file read error occurred
     */
    private List<String> readFile() throws DiscoveryException {
        List<String> lines = new ArrayList<>();
        String line;
        int lineNum = 0;
        if (pathToIni == null) return lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToIni))) {
            while ((line = reader.readLine()) != null) {
                lineNum++;
                lines.add(line.trim());
            }
        } catch (FileNotFoundException e) {
            throw new DiscoveryException(String.format("File <%s> not found.", pathToIni), e.getCause(), e.getStackTrace());
        } catch (IOException e) {
            throw new DiscoveryException(String.format("IO error at line %s : %s.", lineNum, e.getMessage()), e.getCause(), e.getStackTrace());
        }
        return lines;
    }

    /**
     * Returns parsed {@code Installations.ini} backup from file.
     *
     * @param mode {@link InstParser#DB_DISCOVERY_MODE} or {@link InstParser#XSERVER_DISCOVERY_MODE}
     * @return parsed {@code Installations.ini} backup.
     * @throws DiscoveryException if {@code mode} is wrong;
     *                            if file read error occurred;
     *                            if file is in the wrong format
     */
    public List<LinuxDiscoveryData> parse(int mode) throws DiscoveryException {
        if (mode != DB_DISCOVERY_MODE && mode != XSERVER_DISCOVERY_MODE) {
            throw new DiscoveryException(String.format("Wrong parser mode : <value : %s>, expected <%s> or <%s>.",
                    mode, DB_DISCOVERY_MODE, XSERVER_DISCOVERY_MODE));
        }
        HashMap<String, LinuxDiscoveryData> dbMap = new HashMap<>();
        List<String> lines;
        if (data == null) {
            lines = readFile();
        } else {
            lines = data;
        }
        if (lines.size() == 0) {
            throw new DiscoveryException("Wrong file format : file is empty.");
        }
        //search [Installations] section
        int index = 0;
        while (!lines.get(index).equals("[Installations]")) {
            index++;
            if (index > lines.size() - 1) {
                throw new DiscoveryException("Wrong file format : missing [Installations] section.");
            }
        }
        //skip section declaration
        index++;

        //read MaxDB backup
        while (index < lines.size()) {
            //if section ends - stop parsing
            if (lines.get(index).indexOf('[') > -1) break;

            if (lines.get(index).length() > 0) {
                Matcher matcher = Pattern.compile(INST_PAIR_CHECK).matcher(lines.get(index));
                if (!matcher.matches()) {
                    throw new DiscoveryException(String.format("Wrong file <%s> format at line %s : not [Installation] section format.", pathToIni, index + 1));
                }
                String[] parsed = lines.get(index).replace("/sapdb/", "").replace("/db", "").split("=");
                dbMap.put(parsed[0], new LinuxDiscoveryData(parsed[0], parsed[1]));
            }
            index++;
        }

        if (mode == XSERVER_DISCOVERY_MODE) {
            parseForXServer(dbMap, lines, index);
        }

        return new ArrayList<>(dbMap.values());
    }

    /**
     * Parse XServers backup.
     *
     * @param dbMap map with MaxDB instances, where {@code key} is MaxDB name
     * @param lines list of lines from {@code Installations.ini} file
     * @param index line number on which the MaxDB info parsing ended
     * @throws DiscoveryException if backup is in the wrong format
     */
    private void parseForXServer(HashMap<String, LinuxDiscoveryData> dbMap, List<String> lines, int index) throws DiscoveryException {
        final Pattern pattern = Pattern.compile(PARAMS_SEC_CHECK);
        Matcher matcher;
        String[] parsed;
        LinuxDiscoveryData current = null;

        //list of already read keys
        List<String> readKeys = new ArrayList<>();

        while (index < lines.size()) {
            matcher = pattern.matcher(lines.get(index));
            if (matcher.matches()) {
                readKeys.clear();

                current = dbMap.get(lines.get(index).replace("[Params-/sapdb/", "").replace("/db]", ""));
                if (current == null) {
                    throw new DiscoveryException(String.format("Wrong file <%s> format : unknown DB <%s> line %s.", pathToIni,
                            lines.get(index), index + 1));
                }
            } else {
                if (lines.get(index).contains("=")) {
                    if (current == null) {
                        throw new DiscoveryException(String.format("Wrong file <%s> format : key-value pair for unknown [Params-...] section, line %s.",
                                pathToIni, index + 1));
                    }
                    parsed = lines.get(index).split("=");
                    if (parsed.length != 2) {
                        throw new DiscoveryException(String.format("Wrong file <%s> format : cant get key-value pair in line %s.",
                                pathToIni, index + 1));
                    }
                    try {
                        if (readKeys.contains(parsed[0])) {
                            throw new DiscoveryException(String.format("Wrong file <%s> format : key <%s> occurs 2 times in one section line %s.",
                                    pathToIni, parsed[0], index + 1));
                        } else {
                            readKeys.add(parsed[0]);
                        }
                        switch (parsed[0]) {
                            case PORT:
                                current.setPort(Integer.parseInt(parsed[1]));
                                break;
                            case NI_PORT:
                                current.setNiPort(Integer.parseInt(parsed[1]));
                                break;
                            case SSL_PORT:
                                current.setSslPort(Integer.parseInt(parsed[1]));
                                break;
                        }
                    } catch (NumberFormatException e) {
                        throw new DiscoveryException(String.format("Wrong file <%s> format : cant parse <%s> to Int in line %s.",
                                pathToIni, parsed[1], index + 1), e.getCause(), e.getStackTrace());
                    }
                }
            }
            index++;
        }
    }
}
