package com.github.onlycrab.maxdbmon.extractor.connector;

/**
 * Available commands to query for MaxDB instance.
 * <blockquote>
 * OPERATIONAL_STATE- get DB operational(power) state (online, offline, etc.)
 * INFO_STATE       - get DB instance common data,
 * INFO_DATA        - get DB DataArea data,
 * INFO_CACHES      - get DB instance caches hit rate,
 * INFO_LOG         - get DB LogData data,
 * INFO_LOCKS       - get DB locks statistic,
 * INFO_PARAMS      - get DB instance parameters,
 * BACKUP_HISTORY   - get DB backup history.
 * </blockquote>
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public enum DBMCommand {
    OPERATIONAL_STATE(new String[]{"db_state", null}),
    INFO_STATE(new String[]{"info state", "info_next"}),
    INFO_DATA(new String[]{"info data", "info_next"}),
    INFO_CACHES(new String[]{"info caches", "info_next"}),
    INFO_LOG(new String[]{"info log", "info_next"}),
    INFO_LOCKS(new String[]{"info locks", "info_next"}),
    INFO_PARAMS(new String[]{"info params", "info_next"}),
    BACKUP_HISTORY(new String[]{"backup_history_list -c LABEL,START,STOP,RC,ERROR -Inverted", "backup_history_listnext"});

    private final String[] command;

    DBMCommand(String[] command) { this.command = command; }

    /**
     * Returns SAPDBC command text.
     *
     * @return SAPDBC command text
     */
    public String getCommandText() {
        return command[0];
    }

    /**
     * Returns SAPDBC command text for next page.
     *
     * @return SAPDBC command text for next page
     */
    public String getCommandTextNext() {
        return command[1];
    }
}