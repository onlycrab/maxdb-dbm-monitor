package com.github.onlycrab.maxdbmon.runner;

import com.github.onlycrab.argParser.arguments.Argument;
import com.github.onlycrab.argParser.arguments.ArgumentParser;
import com.github.onlycrab.argParser.arguments.ArgumentStorage;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import com.github.onlycrab.common.SimpleIniOper;
import com.github.onlycrab.maxdbmon.discovery.Discovery;
import com.github.onlycrab.maxdbmon.discovery.DiscoveryException;
import com.github.onlycrab.maxdbmon.extractor.Extractor;
import com.github.onlycrab.maxdbmon.extractor.connector.DBMConnector;
import com.github.onlycrab.maxdbmon.xserver.XServerChecker;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * The class that executes console commands.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
@SuppressWarnings("WeakerAccess")
public class Executor {
    /**
     * Project title.
     */
    public static final String TITLE = "MaxDB DataBaseManager Monitor";
    /**
     * Arguments data resource name.
     */
    private static final String ARG_DATA = "Arguments.xml";
    /**
     * Object to extract data from MaxDB instance.
     */
    protected Extractor extractor;
    /**
     * Object to check status of XServer instance.
     */
    protected XServerChecker xServerChecker;
    /**
     * Object to discovery MaxDB and XServer instances.
     */
    protected Discovery discovery;
    /**
     * Discovery mod.
     *
     * @see Discovery
     */
    protected int discoveryMod;
    /**
     * Names of MaxDB instances for exclude or only discovery.
     *
     * @see Discovery
     */
    protected String dbNames;
    /**
     * Storage for arguments data.
     */
    private ArgumentStorage storage = null;

    public Executor() throws DiscoveryException {
        extractor = new Extractor();
        xServerChecker = new XServerChecker();
        discovery = new Discovery();
        dbNames = null;
        discoveryMod = Discovery.MODE_NAMES_NONE;
    }

    /**
     * Returns error message as JSON.
     *
     * @param msg message for printing
     * @return error message as JSON
     */
    static String printError(String msg) {
        if (msg != null) {
            return String.format("{\"error\":\"%s\"}", msg);
        } else {
            return "{\"error\":\"<null>\"}";
        }
    }

    /**
     * Returns info about project and version.
     *
     * @return info about project and version
     */
    private String getProjectVersion(){
        try {
            InputStream mfStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
            Manifest mf = new Manifest();
            mf.read(mfStream);
            return String.format("%s (%s) version \"%s\"",
                    TITLE,
                    mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE),
                    mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION)
            );
        } catch (Exception e) {
            return String.format("%s version \"unknown\" (error:%s)", TITLE, e.getMessage());
        }
    }

    /**
     * Initialize executor: create argument data storage and parse console args.
     *
     * @param args console arguments
     * @return help text about project or {@code null} if no help argument found
     * @throws Exception if SAPDBC classes not loaded;
     *                  if an error occurred while loading argument structure from internal resource;
     *                  if an error occurred while reading and parsing argument values from external file;
     *                  if an arguments requirements(dependencies) are not satisfied;
     *                  if there are conflicts between some arguments
     */
    protected String init(String[] args) throws Exception {
        //Create argument storage
        storage = new ArgumentStorage();
        storage.read(storage.getClass().getClassLoader().getResourceAsStream(ARG_DATA), null);

        //Print help if no input arguments
        if (args == null) {
            return storage.getHelp();
        } else if (args.length == 0) {
            return storage.getHelp();
        } else if (storage.isDeclared(ArgumentName.Short.VERSION)){
            return getProjectVersion();
        }

        //Check if SAPDBC library loaded
        try {
            Class.forName("com.sap.dbtech.powertoys.DBM");
            Class.forName("com.sap.dbtech.powertoys.DBMException");
            Class.forName("com.sap.dbtech.rte.comm.RTEException");
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(String.format("SAPDBC library is missing : <%s>.", e.getMessage()));
        }

        //Parse arguments values to storage
        ArgumentParser.parse(storage, args);

        //Read additional argument values from file if needed
        Argument externalFile = storage.getArgument(ArgumentName.Short.EXTERNAL_FILE);
        if (externalFile.isFilled()) {
            try {
                ArgumentParser.parse(storage, SimpleIniOper.read(externalFile.getValue(), true, null));
            } catch (ArgumentException | IOException e) {
                throw new Exception("Cant read arguments from external file : %s" + e.getMessage());
            }
        }

        //Print help if needed
        String help = storage.getSystemHelp(ArgumentStorage.HELP_SHORT_NAME);
        if (help != null) {
            return help;
        } else {
            help = storage.getSystemHelp(ArgumentStorage.HELP_LONG_NAME);
            if (help != null) {
                return help;
            } else if (storage.isDeclared(ArgumentName.Short.VERSION)){
                return getProjectVersion();
            }
        }

        //Check conditions
        if (!storage.isRequireFilled()) {
            throw new ArgumentException(storage.getMessage());
        }
        if (storage.isConflict()) {
            throw new ArgumentException(storage.getMessage());
        }

        return null;
    }

    /**
     * Execute console command.
     *
     * @param args console arguments
     * @return JSON result of command execution
     */
    public String execute(String[] args) {
        try {
            String help = init(args);
            if (help != null){
                return help;
            }
        } catch (Exception e) {
            return printError(e.getMessage());
        }

        //Execute commands
        try {
            if (storage.isDeclared(ArgumentName.Short.REQUEST_OPERSTATE)) {
                //Get MaxDB operational state
                initExtractor(storage);
                return extractor.getOperStateRawJSON(storage.getValue(ArgumentName.Short.MACROS));
            } else if (storage.isDeclared(ArgumentName.Short.REQUEST_INFOSTATE)) {
                //Get MaxDB info state
                initExtractor(storage);
                return extractor.getStateRawJSON(storage.getValue(ArgumentName.Short.MACROS));
            } else if (storage.isDeclared(ArgumentName.Short.REQUEST_INFOCACHE)) {
                //Get MaxDB info cache
                initExtractor(storage);
                return extractor.getCacheHitRateRawJSON(storage.getValue(ArgumentName.Short.MACROS));
            }  else if (storage.isDeclared(ArgumentName.Short.REQUEST_INFOLOG)) {
                //Get MaxDB info LogArea
                initExtractor(storage);
                return extractor.getLogRawJSON(storage.getValue(ArgumentName.Short.MACROS));
            } else if (storage.isDeclared(ArgumentName.Short.REQUEST_INFOLOCKS)) {
                //Get MaxDB info locks
                initExtractor(storage);
                return extractor.getLocksRawJSON(storage.getValue(ArgumentName.Short.MACROS));
            } else if (storage.isDeclared(ArgumentName.Short.REQUEST_BACKUP)) {
                //Get MaxDB last backup info
                initExtractor(storage);
                return extractor.getBackupRawJSON(storage.getValue(ArgumentName.Short.MACROS));
            } else if (storage.isDeclared(ArgumentName.Short.REQUEST_PARAMS_DIFFERENCE)) {
                //Get MaxDB parameters difference
                initExtractor(storage);
                return extractor.getParamsDiffRawJSON(storage.getValue(ArgumentName.Short.MACROS));
            } else if (storage.isDeclared(ArgumentName.Short.REQUEST_DB_LIST) || storage.isDeclared(ArgumentName.Short.REQUEST_XSERVER_LIST)) {
                //If request to discover MaxDB or XServer - parse names and mode
                if (storage.isFilled(ArgumentName.Short.EXCLUDE_DATABASE) && storage.isFilled(ArgumentName.Short.ONLY_DATABASE)) {
                    return printError(String.format("Conflict at discovery mode : selected EXCLUDE and ONLY mode at one time, arguments <%s(%s)> <%s(%s)>",
                            ArgumentName.Short.EXCLUDE_DATABASE, ArgumentName.Full.EXCLUDE_DATABASE,
                            ArgumentName.Short.ONLY_DATABASE, ArgumentName.Full.ONLY_DATABASE));
                }
                if (storage.isFilled(ArgumentName.Short.EXCLUDE_DATABASE)) {
                    discoveryMod = Discovery.MODE_NAMES_EXCLUDE;
                    dbNames = storage.getValue(ArgumentName.Short.EXCLUDE_DATABASE);
                } else if (storage.isFilled(ArgumentName.Short.ONLY_DATABASE)) {
                    discoveryMod = Discovery.MODE_NAMES_ONLY;
                    dbNames = storage.getValue(ArgumentName.Short.ONLY_DATABASE);
                }

                //Execute discover process
                if (storage.isDeclared(ArgumentName.Short.REQUEST_DB_LIST)) {
                    if (storage.isFilled(ArgumentName.Short.DATABASES_INI)) {
                        discovery.init(storage.getValue(ArgumentName.Short.DATABASES_INI), dbNames, discoveryMod);
                    } else {
                        discovery.init(dbNames, discoveryMod);
                    }
                    return discovery.getMaxDB();
                } else {
                    if (storage.isFilled(ArgumentName.Short.DATABASES_INI)) {
                        discovery.init(storage.getValue(ArgumentName.Short.DATABASES_INI), dbNames, discoveryMod);
                    } else {
                        discovery.init(dbNames, discoveryMod);
                    }
                    return discovery.getXServer();
                }
            } else if (storage.isDeclared(ArgumentName.Short.REQUEST_XSERVER_STATE)) {
                //Get XServer state
                initChecker(storage);
                return xServerChecker.getState();
            } else {
                return printError("Request parameter missing.");
            }
        } catch (Exception e) {
            return printError(e.getMessage());
        }
    }

    /**
     * Check if argument is filled. If argument is not filled throws {@link IllegalArgumentException}.
     *
     * @param storage   argument storage object
     * @param nameShort short argument name
     * @param nameFull  full argument name
     * @throws IllegalArgumentException if argument is not filled
     */
    private void checkIsArgumentFilled(ArgumentStorage storage, String nameShort, String nameFull) throws IllegalArgumentException {
        if (!storage.isFilled(nameShort)) {
            throw new IllegalArgumentException(String.format("Argument <%s(%s)> value are missing.", nameShort, nameFull));
        }
    }

    /**
     * Initialize instance of {@link Extractor}.
     *
     * @param storage argument storage object
     * @throws IllegalArgumentException if arguments required to connect to MaxDB are missing
     */
    private void initExtractor(ArgumentStorage storage) throws IllegalArgumentException {
        checkIsArgumentFilled(storage, ArgumentName.Short.NODE, ArgumentName.Full.NODE);
        checkIsArgumentFilled(storage, ArgumentName.Short.DATABASE_NAME, ArgumentName.Full.DATABASE_NAME);
        checkIsArgumentFilled(storage, ArgumentName.Short.USER, ArgumentName.Full.USER);
        checkIsArgumentFilled(storage, ArgumentName.Short.PASSWORD, ArgumentName.Full.PASSWORD);
        extractor.setConnector(new DBMConnector(
                storage.getValue(ArgumentName.Short.NODE),
                storage.getValue(ArgumentName.Short.DATABASE_NAME),
                storage.getValue(ArgumentName.Short.USER),
                storage.getValue(ArgumentName.Short.PASSWORD)
        ));
    }

    /**
     * Initialize instance of {@link XServerChecker}.
     *
     * @param storage argument storage object
     * @throws IllegalArgumentException if XServer host or port argument are missing
     */
    private void initChecker(ArgumentStorage storage) throws IllegalArgumentException {
        checkIsArgumentFilled(storage, ArgumentName.Short.NODE, ArgumentName.Full.NODE);
        checkIsArgumentFilled(storage, ArgumentName.Short.XSERVER_PORT, ArgumentName.Full.XSERVER_PORT);
        xServerChecker.setHost(storage.getValue(ArgumentName.Short.NODE));
        xServerChecker.setPort(storage.getValue(ArgumentName.Short.XSERVER_PORT));
    }
}
