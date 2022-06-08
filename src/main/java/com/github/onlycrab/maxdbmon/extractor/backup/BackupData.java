package com.github.onlycrab.maxdbmon.extractor.backup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Class to represent a single entry from the backup history.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class BackupData {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final BackupSource source;
    private final int number;
    private final int returnCode;
    private final String returnMessage;
    private Date startDate;
    private Date endDate;

    public BackupData(BackupSource source, int number, String startDate, String endDate, int returnCode, String returnMessage) {
        this.source = source;
        this.number = number;
        this.returnCode = returnCode;
        if (returnMessage != null){
            this.returnMessage = returnMessage;
        } else {
            this.returnMessage = "";
        }
        try {
            this.startDate = formatter.parse(startDate);
        } catch (ParseException e) {
            this.startDate = null;
        }
        try {
            this.endDate = formatter.parse(endDate);
        } catch (ParseException e) {
            this.endDate = null;
        }
    }

    public BackupSource getSource() {
        return source;
    }

    public int getNumber() {
        return number;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public Date getStartDate() {
        if (startDate != null) {
            return startDate;
        } else {
            return new Date((new Calendar.Builder().setDate(2000, 0, 1).setTimeOfDay(0, 0, 0).build()).getTimeInMillis());
        }
    }

    public Date getEndDate() {
        if (endDate != null) {
            return endDate;
        } else {
            return new Date((new Calendar.Builder().setDate(2000, 0, 1).setTimeOfDay(0, 0, 0).build()).getTimeInMillis());
        }
    }

    public long getDuration() {
        if (startDate != null && endDate != null) {
            return Math.abs(endDate.getTime() - startDate.getTime()) / 1000;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;

        if (!(obj instanceof BackupData)) return false;
        BackupData another = (BackupData) obj;

        if (!source.equals(another.getSource())) return false;
        if (number != another.getNumber()) return false;
        if (!getStartDate().equals(another.getStartDate())) return false;
        if (!getEndDate().equals(another.getEndDate())) return false;
        if (returnCode != another.getReturnCode()) return false;
        return returnMessage.equals(another.getReturnMessage());
    }

    @Override
    public String toString() {
        String emptyDate = "2000-01-01 00:00:00";
        return String.format("Source : <%s>, number : <%s>, start at : <%s>, end at : <%s>, code : <%s>, msg : <%s>",
                source,
                number,
                startDate != null ? formatter.format(startDate) : emptyDate,
                endDate != null ? formatter.format(endDate) : emptyDate,
                returnCode,
                returnMessage
        );
    }
}
