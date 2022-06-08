package com.github.onlycrab.maxdbmon.extractor.backup;

/**
 * MaxDB backup types.
 * <blockquote>
 * DAT  - full data backup,
 * LOG  - log backup,
 * PAG  - incremental data backup.
 * </blockquote>
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public enum BackupSource {
    DAT,
    LOG,
    PAG
}
