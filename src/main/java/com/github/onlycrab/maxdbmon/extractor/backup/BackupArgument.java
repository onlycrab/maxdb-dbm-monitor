package com.github.onlycrab.maxdbmon.extractor.backup;

/**
 * Available arguments for backup state requests.
 * <blockquote>
 * SUCCESSFUL   - is last backup completed successfully,
 * NUMBER       - get last backup number,
 * DURATION     - get last backup duration,
 * START_DATE   - get last backup start date,
 * START_TIME   - get last backup start time,
 * END_DATE     - get last backup end date,
 * END_TIME     - get last backup end time,
 * MESSAGE      - get last backup message.
 * </blockquote>
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public enum BackupArgument {
    SUCCESSFUL,
    NUMBER,
    DURATION,
    START_DATE,
    START_TIME,
    END_DATE,
    END_TIME,
    MESSAGE
}
