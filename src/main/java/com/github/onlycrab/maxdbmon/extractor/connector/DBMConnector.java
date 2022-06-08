package com.github.onlycrab.maxdbmon.extractor.connector;

import com.sap.dbtech.powertoys.DBM;
import com.sap.dbtech.powertoys.DBMException;
import com.sap.dbtech.rte.comm.RTEException;

/**
 * Connector to MaxDB instance using SAPDBC driver.
 */
@SuppressWarnings("WeakerAccess,CanBeFinal")
public class DBMConnector {
    private static final String CONTINUE_FLAG = "CONTINUE";
    private static final String END_FLAG = "END";
    protected String host;
    protected String dbName;
    protected String login;
    protected String password;
    protected DBM session;

    public String getDbName(){
        return dbName;
    }

    /**
     * Create empty connector. Use only for tests.
     */
    protected DBMConnector(){
        host = "";
        dbName = "";
        login = "";
        password = "";
    }

    /**
     * Create new connector <strong>without</strong> opening a connection.
     *
     * @param host     MaxDB host
     * @param dbName   MaxDB instance name
     * @param login    login to MaxDB
     * @param password password tp MaxDB
     */
    public DBMConnector(String host, String dbName, String login, String password) {
        this.host = host;
        this.dbName = dbName;
        this.login = login;
        this.password = password;
        session = null;
    }

    /**
     * Execute command to MaxDB instance (1. open connection, 2. execute command, 3.close connection).
     *
     * @param command MaxDB command
     * @return result of command
     * @throws DBMConnectionException if command is invalid;
     *                                if user/password to DB is invalid;
     *                                if system error occurred while establishing connection
     */
    public String execute(DBMCommand command) throws DBMConnectionException {
        open();
        try {
            return executeOnly(command);
        } finally {
            close();
        }
    }

    /**
     * Only execute command to MaxDB instance.
     *
     * @param command MaxDB command
     * @return result of command
     * @throws DBMConnectionException if command is invalid;
     *                                if user/password to DB is invalid;
     *                                if system error occurred while establishing connection
     */
    protected String executeOnly(DBMCommand command) throws DBMConnectionException {
        if (session == null) {
            throw new DBMConnectionException(String.format("Cant execute DBMCommand to database <%s/%s> : connection closed.",
                    host, dbName));
        }
        try {
            StringBuilder sb = new StringBuilder();
            String result;
            while (true){
                //if it is first page - execute command, otherwise - execute next page command
                if (sb.length() == 0) {
                    result = removeDuplicateLineBreak(session.cmd(command.getCommandText()));
                } else {
                    result = removeDuplicateLineBreak(session.cmd(command.getCommandTextNext()));
                }
                //check CONTINUE and END page flags
                if (result.length() > CONTINUE_FLAG.length()){
                    if (result.startsWith(CONTINUE_FLAG)){
                        sb.append(result.substring(result.indexOf('\n') + 1));
                    } else if (result.startsWith(END_FLAG)){
                        sb.append(result.substring(result.indexOf('\n') + 1));
                        return sb.toString();
                    } else {
                        sb.append(result);
                        return sb.toString();
                    }
                } else if (result.length() > END_FLAG.length()){
                    if (result.startsWith(END_FLAG)){
                        sb.append(result.substring(result.indexOf('\n') + 1));
                        return sb.toString();
                    }
                } else {
                    sb.append(result);
                    return sb.toString();
                }
            }
        } catch (RTEException rteException) {
            session = null;
            throw new DBMConnectionException(rteException, String.format("Cant execute DBMCommand to database <%s/%s> : connection closed, %s",
                    host, dbName, rteException.getMessage()), rteException.getCause(), rteException.getStackTrace());
        } catch (DBMException dbmException) {
            throw new DBMConnectionException(dbmException, String.format("Cant execute DBMCommand to database <%s/%s> : %s",
                    host, dbName, dbmException.getMessage()), dbmException.getCause(), dbmException.getStackTrace());
        }
    }

    /**
     * Open MaxDB session.
     *
     * @throws DBMConnectionException if user/password to DB is invalid;
     *                                if system error occurred while establishing connection
     */
    protected void open() throws DBMConnectionException {
        try {
            session = DBM.dbDBM(host, dbName);
            session.cmd(String.format("user_logon %s,%s", login, password));
        } catch (RTEException rteException) {
            throw new DBMConnectionException(rteException, String.format("Cant open connection to database <%s/%s> : %s.",
                    host, dbName, rteException.getMessage()), rteException.getCause(), rteException.getStackTrace());
        } catch (DBMException dbmException) {
            throw new DBMConnectionException(dbmException, String.format("Cant execute logon command for user <%s> to database <%s/%s> : %s.",
                    login, host, dbName, dbmException.getMessage()), dbmException.getCause(), dbmException.getStackTrace());
        }
    }

    /**
     * Close MaxDB session.
     */
    protected void close() {
        if (session != null) {
            try {
                session.release();
            } catch (RTEException ignore) {
            }
        }
    }

    /**
     * Returns string without duplicate line breaks at the end of this string.
     *
     * @param str target string
     * @return string without duplicate line breaks at the end
     */
    protected final String removeDuplicateLineBreak(String str){
        if (str == null){
            return null;
        } else if (str.length() == 0){
            return str;
        }
        String result = str;
        while (true){
            if (result.length() == 2){
                if (result.equals("\n\n")){
                    return "";
                } else {
                    return result;
                }
            } else if (result.length() < 2){
                return result;
            }
            if (result.substring(result.length() - 2, result.length()).equals("\n\n")){
                result = result.substring(0, result.length() - 1);
            } else {
                if (!result.substring(result.length() - 1).equals("\n")){
                    return result + "\n";
                } else {
                    return result;
                }
            }
        }
    }
}
