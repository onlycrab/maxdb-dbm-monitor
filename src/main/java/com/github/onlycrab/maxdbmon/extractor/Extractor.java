package com.github.onlycrab.maxdbmon.extractor;

import com.github.onlycrab.maxdbmon.extractor.comparator.ParamsComparator;
import com.github.onlycrab.maxdbmon.extractor.connector.DBMCommand;
import com.github.onlycrab.maxdbmon.extractor.connector.DBMConnectionException;
import com.github.onlycrab.maxdbmon.extractor.connector.DBMConnector;
import com.github.onlycrab.maxdbmon.extractor.parser.*;
import com.github.onlycrab.maxdbmon.extractor.state.OperationalState;

import java.io.IOException;
import java.util.Map;

/**
 * Bus class designed to receive requests, extract data and then return it.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class Extractor {
    /**
     * Operational state data parser.
     */
    private final OperationalStateParser operationalStateParser;

    /**
     * Zabbix macros data parser.
     */
    private final MacrosParser macrosParser;

    /**
     * Connector to MaxDB instance.
     */
    private DBMConnector connector = null;

    /**
     * MaxDB parameters comparator.
     */
    ParamsComparator paramsComparator;

    /**
     * Create new extractor instance.
     */
    public Extractor() {
        operationalStateParser = new OperationalStateParser();
        macrosParser = new MacrosParser();

        paramsComparator = new ParamsComparator();
    }

    /**
     * Set connector to MaxDB instance.
     *
     * @param connector connector to MaxDB instance
     */
    public void setConnector(DBMConnector connector) {
        this.connector = connector;
    }

    /**
     * Returns MaxDB instance operational(power) state as JSON.
     *
     * @param macros zabbix macros
     * @return MaxDB instance operational(power) state as JSON.
     */
    public String getOperStateRawJSON(String macros) {
        StringBuilder sb = new StringBuilder("{");
        boolean isFirst = true;
        if (connector == null) {
            sb.append("\"error\":\"DBMConnector is <null>\"");
        } else {
            try {
                String result = connector.execute(DBMCommand.OPERATIONAL_STATE);
                OperationalStateParser operationalStateParser = new OperationalStateParser();
                sb.append(String.format("\"operstate\":\"%s\"", operationalStateParser.getOperationalStateCode(result)));
                isFirst = false;
                sb.append(", \"error\":\"\"");
            } catch (DBMConnectionException e) {
                sb.append(DBMErrorInfo(e));
            } catch (Exception e) {
                if (!isFirst) {
                    sb.append(", ");
                }
                sb.append(String.format("\"error\":\"Unexpected error : %s\"", e.getMessage().replace('"', '\'')));
            }
        }
        addMacrosData(sb, macros);
        return sb.append("}").toString();
    }

    /**
     * Returns MaxDB instance all state info values as JSON.
     *
     * @param macros zabbix macros
     * @return MaxDB instance all state info values as JSON
     */
    public String getStateRawJSON(String macros) {
        StringBuilder sb = new StringBuilder("{");
        boolean isFirst = true;
        if (connector == null) {
            sb.append("\"error\":\"DBMConnector is <null>\"");
        } else {
            try {
                //info command can be executed only in one of the operational states ADMIN, STANDBY or ONLINE
                int code = operationalStateParser.getOperationalStateCode(connector.execute(DBMCommand.OPERATIONAL_STATE));
                if (OperationalState.ADMIN.getCode() == code || OperationalState.STANDBY.getCode() == code || OperationalState.ONLINE.getCode() == code) {
                    String result = connector.execute(DBMCommand.INFO_STATE);
                    StateMdbParser stateMdbParser = new StateMdbParser();
                    for (Map.Entry<String, String> entry : stateMdbParser.getDataMap(result).entrySet()) {
                        addEntryData(entry, sb, isFirst);
                        isFirst = false;
                    }
                    result = connector.execute(DBMCommand.INFO_DATA);
                    InfoDataParser infoDataParser = new InfoDataParser();
                    if (isFirst){
                        sb.append(String.format("\"data_volumes_count\":\"%s\"", infoDataParser.getDataVolumes(result)));
                        isFirst = false;
                    } else {
                        sb.append(String.format(", \"data_volumes_count\":\"%s\"", infoDataParser.getDataVolumes(result)));
                    }
                    sb.append(", \"error\":\"\"");
                } else {
                    sb.append(String.format("\"error\":\"Cant get data : database state <%s>\"", OperationalState.fromCode(code)));
                }
            } catch (DBMConnectionException e) {
                if (!isFirst) {
                    sb.append(", ");
                }
                sb.append(DBMErrorInfo(e));
            } catch (Exception e) {
                if (!isFirst) {
                    sb.append(", ");
                }
                sb.append(String.format("\"error\":\"Unexpected error : %s\"", e.getMessage().replace('"', '\'')));
            }
        }
        addMacrosData(sb, macros);
        return sb.append("}").toString();
    }

    /**
     * Returns MaxDB instance caches hit rate as JSON.
     *
     * @param macros zabbix macros
     * @return MaxDB instance caches hit rate as JSON
     */
    public String getCacheHitRateRawJSON(String macros) {
        return getRawJSON(macros, DBMCommand.INFO_CACHES, new CachesMdbParser());
    }

    /**
     * Returns MaxDB instance LogArea info as JSON.
     *
     * @param macros zabbix macros
     * @return MaxDB instance LogArea info as JSON
     */
    public String getLogRawJSON(String macros) {
        return getRawJSON(macros, DBMCommand.INFO_LOG, new LogMdbParser());
    }

    /**
     * Returns MaxDB instance locks info as JSON.
     *
     * @param macros zabbix macros
     * @return MaxDB instance locks info as JSON
     */
    public String getLocksRawJSON(String macros) { return getRawJSON(macros, DBMCommand.INFO_LOCKS, new LocksMdbParser()); }

    /**
     * Common method for standard use of {@link MdbParser}.
     *
     * @param macros zabbix macros
     * @param command DBMCommand for SAPDBC driver
     * @param mdbParser instance of parser
     * @return parsed MaxDB data as JSON
     */
    private String getRawJSON(String macros, DBMCommand command, MdbParser mdbParser){
        StringBuilder sb = new StringBuilder("{");
        boolean isFirst = true;
        if (connector == null) {
            sb.append("\"error\":\"DBMConnector is <null>\"");
        } else {
            try {
                //info command can be executed only in one of the operational states ADMIN, STANDBY or ONLINE
                int code = operationalStateParser.getOperationalStateCode(connector.execute(DBMCommand.OPERATIONAL_STATE));
                if (OperationalState.ADMIN.getCode() == code || OperationalState.STANDBY.getCode() == code || OperationalState.ONLINE.getCode() == code) {
                    String result = connector.execute(command);
                    for (Map.Entry<String, String> entry : mdbParser.getDataMap(result).entrySet()) {
                        addEntryData(entry, sb, isFirst);
                        isFirst = false;
                    }
                    if (!isFirst) {
                        sb.append(", ");
                    }
                    sb.append("\"error\":\"\"");
                } else {
                    sb.append(String.format("\"error\":\"Cant get data : database state <%s>\"", OperationalState.fromCode(code)));
                }
            } catch (DBMConnectionException e) {
                sb.append(DBMErrorInfo(e));
            } catch (Exception e) {
                if (!isFirst) {
                    sb.append(", ");
                }
                sb.append(String.format("\"error\":\"Unexpected error : %s\"", e.getMessage().replace('"', '\'')));
            }
        }
        addMacrosData(sb, macros);
        return sb.append("}").toString();
    }

    /**
     * Returns MaxDB instance all last backup state values as JSON.
     *
     * @param macros zabbix macros
     * @return MaxDB instance all last backup state values as JSON
     */
    public String getBackupRawJSON(String macros) {
        StringBuilder sb = new StringBuilder("{");
        boolean isFirst = true;
        if (connector == null) {
            sb.append("\"error\":\"DBMConnector is <null>\"");
        } else {
            try {
                String result = connector.execute(DBMCommand.BACKUP_HISTORY);
                BackupHistoryParser backupHistoryParser = new BackupHistoryParser();
                for (Map.Entry<String, String> entry : backupHistoryParser.getDataMap(result).entrySet()) {
                    addEntryData(entry, sb, isFirst);
                    isFirst = false;
                }
                if (!isFirst) {
                    sb.append(", ");
                }
                sb.append("\"error\":\"\"");
            } catch (DBMConnectionException e) {
                sb.append(DBMErrorInfo(e));
            } catch (Exception e) {
                if (!isFirst) {
                    sb.append(", ");
                }
                sb.append(String.format("\"error\":\"Unexpected error : %s\"", e.getMessage().replace('"', '\'')));
            }
        }
        addMacrosData(sb, macros);
        return sb.append("}").toString();
    }

    /**
     * Returns difference in parameter map between current and previous state as JSON.
     * @param macros zabbix macros
     * @return difference in parameter map between current and previous state as JSON
     */
    public String getParamsDiffRawJSON(String macros){
        StringBuilder sb = new StringBuilder("{");
        if (connector == null) {
            sb.append("\"error\":\"DBMConnector is <null>\"");
        } else {
            try {
                //info command can be executed only in one of the operational states ADMIN, STANDBY or ONLINE
                int code = operationalStateParser.getOperationalStateCode(connector.execute(DBMCommand.OPERATIONAL_STATE));
                if (OperationalState.ADMIN.getCode() == code || OperationalState.STANDBY.getCode() == code || OperationalState.ONLINE.getCode() == code) {
                    String result = connector.execute(DBMCommand.INFO_PARAMS);
                    paramsComparator.init(connector.getDbName(), result);
                    sb.append(String.format("\"diff\":\"%s\", \"error\":\"\"", paramsComparator.getDifference()));
                } else {
                    sb.append(String.format("\"error\":\"Cant get data : database state <%s>\"", OperationalState.fromCode(code)));
                }
            } catch (DBMConnectionException e) {
                sb.append(DBMErrorInfo(e));
            } catch (SecurityException | IOException e){
                sb.append(String.format("\"error\":\"Cant get data from temporary file : %s\"", e.getMessage()));
            } catch (Exception e) {
                sb.append(String.format("\"error\":\"Unexpected error : %s\"", e.getMessage().replace('"', '\'')));
            }
        }
        addMacrosData(sb, macros);
        return sb.append("}").toString();
    }

    /**
     * Add entry data to JSON string builder.
     *
     * @param entry key-value pair
     * @param sb JSON string builder
     * @param isFirst is this key-value pair first
     */
    private void addEntryData(Map.Entry<String, String> entry, StringBuilder sb, boolean isFirst){
        if (!isFirst) {
            sb.append(", ");
        }
        sb.append(String.format("\"%s\":\"%s\"", entry.getKey(), entry.getValue()));
    }

    /**
     * Add macros key-value pairs to JSON string builder.
     *
     * @param sb JSON string builder
     * @param macros zabbix macros
     */
    private void addMacrosData(StringBuilder sb, String macros){
        if (macros == null){
            return;
        }
        for (Map.Entry<String, String> entry : macrosParser.getDataMap(macros).entrySet()){
            sb.append(", ");
            sb.append(String.format("\"%s\":\"%s\"", entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Returns detailed info about {@code DBMConnectionException}.
     *
     * @param e instance of {@link DBMConnectionException}
     * @return detailed info about {@code DBMConnectionException.
     */
    private String DBMErrorInfo(DBMConnectionException e) {
        return String.format("\"error\":\"DBM error : <msg : %s>, <errorCode : %s>, <errorID : %s>.\"",
                e.getMessage().replace('"', '\''), e.getErrorCode(), e.getErrorID());
    }
}
