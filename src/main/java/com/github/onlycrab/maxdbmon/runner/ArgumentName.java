package com.github.onlycrab.maxdbmon.runner;

/**
 * Names of all available arguments.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
class ArgumentName {
    /**
     * Short names of all available arguments.
     */
    public static class Short {
        public static final String REQUEST_OPERSTATE = "ros";
        public static final String REQUEST_INFOSTATE = "ris";
        public static final String REQUEST_INFOCACHE = "ric";
        public static final String REQUEST_INFOLOG = "rilg";
        public static final String REQUEST_INFOLOCKS = "rilc";
        public static final String REQUEST_BACKUP = "rb";
        public static final String REQUEST_PARAMS_DIFFERENCE = "rpd";
        public static final String REQUEST_DB_LIST = "rdl";
        public static final String REQUEST_XSERVER_LIST = "rxl";
        public static final String REQUEST_XSERVER_STATE = "rxs";

        public static final String NODE = "n";
        public static final String DATABASE_NAME = "d";
        public static final String USER = "u";
        public static final String PASSWORD = "p";
        public static final String DATABASES_INI = "di";
        public static final String XSERVER_PORT = "xp";

        public static final String EXCLUDE_DATABASE = "ed";
        public static final String ONLY_DATABASE = "od";

        public static final String EXTERNAL_FILE = "ef";

        public static final String MACROS = "m";

        public static final String VERSION = "v";
    }

    /**
     * Long names of all available arguments.
     */
    @SuppressWarnings("unused")
    public static class Full {
        public static final String REQUEST_OPERSTATE = "request-oper-state";
        public static final String REQUEST_INFOSTATE = "request-info-state";
        public static final String REQUEST_INFOCACHE = "request-info-cache";
        public static final String REQUEST_INFOLOG = "request-info-log";
        public static final String REQUEST_INFOLOCKS = "request-info-locks";
        public static final String REQUEST_BACKUP = "request-backup";
        public static final String REQUEST_PARAMS_DIFFERENCE = "request-params-difference";
        public static final String REQUEST_DB_LIST = "request-database-list";
        public static final String REQUEST_XSERVER_LIST = "request-xserver-list";
        public static final String REQUEST_XSERVER_STATE = "request-xserver-infostate";

        public static final String NODE = "node";
        public static final String DATABASE_NAME = "database-name";
        public static final String USER = "user";
        public static final String PASSWORD = "password";
        public static final String DATABASES_INI = "databases-ini";
        public static final String XSERVER_PORT = "xserver-port";

        public static final String EXCLUDE_DATABASE = "exclude-database";
        public static final String ONLY_DATABASE = "only-database";

        public static final String EXTERNAL_FILE = "external-file";

        public static final String MACROS = "macros";

        public static final String VERSION = "version";
    }
}
