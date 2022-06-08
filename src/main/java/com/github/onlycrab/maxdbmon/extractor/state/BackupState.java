package com.github.onlycrab.maxdbmon.extractor.state;

/**
 * List of possible outcomes of the backup process.
 * <blockquote>
 * UNSUCCESS(0) - failed due to an error ,
 * SUCCESS(1)   - success,
 * NO_DATA(2)   - backup records not found.
 * </blockquote>
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public enum BackupState {
    UNSUCCESS("0"),
    SUCCESS("1"),
    NO_DATA("2");

    private final String code;

    BackupState(String code) {
        this.code = code;
    }

    /**
     * Returns backup state numeric code as string.
     *
     * @return backup state numeric code as string
     */
    public String getCode() {
        return code;
    }
}
