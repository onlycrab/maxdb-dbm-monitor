package com.github.onlycrab.maxdbmon.extractor.parser;

import com.github.onlycrab.common.UtilitiesString;
import com.github.onlycrab.maxdbmon.extractor.backup.BackupArgument;
import com.github.onlycrab.maxdbmon.extractor.backup.BackupData;
import com.github.onlycrab.maxdbmon.extractor.backup.BackupSource;
import com.github.onlycrab.maxdbmon.extractor.connector.DBMCommand;
import com.github.onlycrab.maxdbmon.extractor.state.BackupState;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The methods of this class are intended for parsing the result of executing
 * {@link DBMCommand#BACKUP_HISTORY} command.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class BackupHistoryParser {
    /**
     * Date formatter for zabbix.
     */
    private static final SimpleDateFormat FORMATTER_DATE = new SimpleDateFormat("yyyyMMdd");

    /**
     * Time formatter for zabbix.
     */
    private static final SimpleDateFormat FORMATTER_TIME = new SimpleDateFormat("HHmmss");

    /**
     * Default return value for quantitative data.
     */
    private static final String NO_DATA_VALUE = "0";

    /**
     * Default message for quantitative data.
     */
    private static final String NO_DATA_MESSAGE = "";

    /**
     * Returns map of MaxDB backup info.
     *
     * @param result query command result
     * @return map of MaxDB backup info
     */
    public Map<String, String> getDataMap(String result) {
        ArrayList<BackupData> data = parse(result);

        Map<String, String> dataMap = new HashMap<>();
        BackupData last;
        for (BackupSource source : BackupSource.values()) {
            last = getLast(data, source);
            if (last == null) {
                dataMap.put(source + "_" + BackupArgument.NUMBER.toString(), NO_DATA_VALUE);
                dataMap.put(source + "_" + BackupArgument.DURATION.toString(), NO_DATA_VALUE);
                dataMap.put(source + "_" + BackupArgument.SUCCESSFUL.toString(), BackupState.NO_DATA.getCode());
                dataMap.put(source + "_" + BackupArgument.START_DATE.toString(), NO_DATA_VALUE);
                dataMap.put(source + "_" + BackupArgument.START_TIME.toString(), NO_DATA_VALUE);
                dataMap.put(source + "_" + BackupArgument.END_DATE.toString(), NO_DATA_VALUE);
                dataMap.put(source + "_" + BackupArgument.END_TIME.toString(), NO_DATA_VALUE);
                dataMap.put(source + "_" + BackupArgument.MESSAGE.toString(), NO_DATA_MESSAGE);
            } else {
                dataMap.put(source + "_" + BackupArgument.NUMBER.toString(), String.valueOf(last.getNumber()));
                dataMap.put(source + "_" + BackupArgument.DURATION.toString(), String.valueOf(last.getDuration()));
                dataMap.put(source + "_" + BackupArgument.SUCCESSFUL.toString(), last.getReturnCode() == 0 ? BackupState.SUCCESS.getCode() : BackupState.UNSUCCESS.getCode());
                dataMap.put(source + "_" + BackupArgument.START_DATE.toString(), FORMATTER_DATE.format(last.getStartDate()));
                dataMap.put(source + "_" + BackupArgument.START_TIME.toString(), FORMATTER_TIME.format(last.getStartDate()));
                dataMap.put(source + "_" + BackupArgument.END_DATE.toString(), FORMATTER_DATE.format(last.getEndDate()));
                dataMap.put(source + "_" + BackupArgument.END_TIME.toString(), FORMATTER_TIME.format(last.getEndDate()));
                dataMap.put(source + "_" + BackupArgument.MESSAGE.toString(), last.getReturnCode() == 0 ?
                        last.getReturnMessage().replace('"', '\'') :
                        String.format("Code <%s> : <%s>", last.getReturnCode(), last.getReturnMessage().replace('"', '\''))
                );
            }
        }
        return dataMap;
    }

    /**
     * Returns last backup data object by specific source.
     *
     * @param data   list of backup data objects
     * @param source backup source
     * @return last backup data object by specific source
     */
    private BackupData getLast(ArrayList<BackupData> data, BackupSource source) {
        for (BackupData element : data) {
            if (element.getSource().equals(source)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Parse backup history records into an ordered list. The order of the records is important to get the correct
     * information about the last backup.
     *
     * @param result query command result
     * @return backup history list
     */
    public static ArrayList<BackupData> parse(String result) {
        ArrayList<BackupData> list = new ArrayList<>();
        if (result == null) {
            return list;
        }
        if (result.trim().equals("")) {
            return list;
        }
        String[] array = result.split("\n");
        for (String str : array) {
            BackupData tmp = parseLine(str);
            if (tmp != null) {
                list.add(tmp);
            }
        }
        return list;
    }

    /**
     * Returns backup data object from one backup history record.
     *
     * @param line record from backup history
     * @return backup data object or {@code null} if record format is incorrect
     */
    @Nullable
    public static BackupData parseLine(String line) {
        String[] backupArray = UtilitiesString.trimDuplicateSpaces(line, true).split("\\|");
        //0 - source and number, 1 - start datetime, 2 - end datetime, 3 - return code, 4 - return message
        if (backupArray.length != 5) {
            return null;
        }

        String[] array = backupArray[0].split("_");//0 - backup source, 1 - backup number
        if (array.length != 2) {
            return null;
        }
        BackupSource source;
        try {
            source = BackupSource.valueOf(array[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
        int number;
        try {
            String tmp = UtilitiesString.removePrefix(array[1], "0", true);
            if (tmp.length() > 0) {
                number = Integer.parseInt(UtilitiesString.removePrefix(array[1], "0", true));
            } else {
                number = 0;
            }
        } catch (NumberFormatException e) {
            number = -1;
        }
        int returnCode;
        try {
            returnCode = Integer.parseInt(UtilitiesString.trimDuplicateSpaces(backupArray[3], true));
        } catch (NumberFormatException e) {
            returnCode = -1;
        }

        return new BackupData(source, number, backupArray[1], backupArray[2], returnCode, UtilitiesString.trimDuplicateSpaces(backupArray[4], true));
    }
}
