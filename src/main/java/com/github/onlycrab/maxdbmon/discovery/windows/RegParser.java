package com.github.onlycrab.maxdbmon.discovery.windows;

import com.github.onlycrab.common.UtilitiesString;
import com.github.onlycrab.maxdbmon.discovery.DiscoveryException;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Class for getting backup about installations of MaxDB and XServers from Windows registry.
 *
 * @author Roman Ryncovich
 * @version 0.9
 */
public class RegParser {
    /**
     * Windows Software installation root.
     */
    private static final String[] INSTALLATIONS_ROOT = new String[]{"HKEY_LOCAL_MACHINE\\SOFTWARE", "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node"};

    /**
     * MaxDB installation path.
     */
    private static final String INSTALLATIONS_PATH = "SAP\\SAP DBTech\\Installations";

    /**
     * Windows Services installation path.
     */
    private static final String SERVICES_FULL_PATH = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services";

    /**
     * Returns list of MaxDB instances names.
     * <p>If {@code backup} is {@code null}, then information about MaxDB installations will be read from the Windows registry.</p>
     *
     * @param data list of strings from which backup about MaxDB installations will be read (can be {@code null})
     * @return list of MaxDB instances names
     * @throws DiscoveryException if some error occurred while reading the Windows registry
     */
    public static List<String> getMaxDBList(@Nullable List<String> data) throws DiscoveryException {
        //DB pattern
        final Pattern pattern = Pattern.compile(".+/[A-Za-z]{3}/db$");

        List<String> names = new ArrayList<>();
        List<String> regData;
        if (data == null) {
            regData = regRead(INSTALLATIONS_ROOT[0] + "\\" + INSTALLATIONS_PATH, null);
            if (regData.size() == 0) {
                regData = regRead(INSTALLATIONS_ROOT[1] + "\\" + INSTALLATIONS_PATH, null);
            }
        } else {
            regData = data;
        }
        for (String str : regData) {
            if (!str.contains(INSTALLATIONS_PATH)) continue;
            str = str.substring(str.indexOf(INSTALLATIONS_PATH) + INSTALLATIONS_PATH.length() + 1);
            if (!pattern.matcher(str).matches()) continue;
            String[] parsed = str.split("/");
            names.add(parsed[parsed.length - 2]);
        }
        return names;
    }

    /**
     * Returns map of XServer instances names and ports.
     * <p>If {@code backup} is {@code null}, then information about MaxDB installations will be read from the Windows registry.</p>
     *
     * @param data list of strings from which backup about XServer installations will be read (can be {@code null})
     * @return list of XServer instances names
     * @throws DiscoveryException if some error occurred while reading the Windows registry
     */
    public static Map<String, Integer> getXServerMap(@Nullable List<String> data) throws DiscoveryException {
        //XServer patterns
        final Pattern patternName = Pattern.compile("^XServer-\\d+$");
        final Pattern patternPath = Pattern.compile(".+\\\\[A-Za-z]+\\\\db\\\\pgm\\\\serv\\.exe$");

        HashMap<String, Integer> xMap = new HashMap<>();
        String[] parsed;
        String tmp;
        int port;
        List<String> regData;
        if (data == null) {
            regData = regRead(SERVICES_FULL_PATH, "/s /v ImagePath");
        } else {
            regData = data;
        }
        /*format:
        ...
        HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services\XServer-dddd
            ImagePath    REG_EXPAND_SZ    ...\ADB\db\pgm\serv.exe
        ...
         */
        for (int i = 0; i < regData.size() - 2; i++) {
            //parse 1st line in pair
            if (regData.get(i).length() < 2) {
                continue;
            }
            if (!regData.get(i).substring(0, 2).equals("HK")) continue;
            parsed = regData.get(i).split("\\\\");
            if (parsed.length < 5) {
                continue;
            }
            if (!patternName.matcher(parsed[4]).matches()) {
                continue;
            }
            try {
                port = Integer.parseInt(parsed[4].split("-")[1]);
            } catch (NumberFormatException e) {
                continue;
            }

            //parse 2d line in pair
            tmp = UtilitiesString.trimDuplicateSpaces(regData.get(i + 1), true);
            parsed = tmp.split(" ");
            if (parsed.length < 3) {
                continue;
            }
            if (!patternPath.matcher(parsed[2]).matches()) {
                continue;
            }
            parsed = parsed[2].split("\\\\");
            xMap.put(parsed[parsed.length - 4], port);
            i++;
        }

        return xMap;
    }

    /**
     * Returns result of command {@code reg query} execution by Windows command line interpreter.
     *
     * @param location registry section name
     * @param param    {@code reg query} parameters
     * @return result of command {@code reg query} execution by Windows command line interpreter
     * @throws DiscoveryException if some error occurred while reading the Windows registry
     */
    private static List<String> regRead(String location, String param) throws DiscoveryException {
        List<String> result = new ArrayList<>();
        try {
            String execString = "reg query \"" + location + "\"";
            if (param != null) {
                if (param.length() > 0) {
                    execString = execString + " " + param;
                }
            }
            Process process = Runtime.getRuntime().exec(execString);

            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();

            for (String str : reader.getResult().split("\n")) {
                str = str.replace("\r", "");
                if (str.length() > 0) {
                    result.add(str);
                }
            }
        } catch (Exception e) {
            throw new DiscoveryException(String.format("Discovery exception on registry read operation : %s.", e.getMessage()),
                    e.getCause(), e.getStackTrace());
        }
        return result;
    }

    /**
     * Simply StreamReader from thread.
     */
    private static class StreamReader extends Thread {
        private final InputStream is;
        private final StringWriter sw = new StringWriter();

        StreamReader(InputStream is) {
            this.is = is;
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            } catch (IOException ignore) {
            }
        }

        String getResult() {
            return sw.toString();
        }
    }
}
