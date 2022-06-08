package com.github.onlycrab.maxdbmon.extractor.connector;

import com.sap.dbtech.powertoys.DBMException;
import com.sap.dbtech.rte.comm.RTEException;

/**
 * This exception is thrown when some error occurred in connection to MaxDB instance.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class DBMConnectionException extends Exception {
    private final int errorCode;
    private final String errorID;

    public DBMConnectionException(String message) {
        super(message);
        this.errorCode = 0;
        this.errorID = "";
    }

    public DBMConnectionException(DBMException dbmEx, String msg, Throwable cause, StackTraceElement[] stackTrace) {
        super(msg, cause);
        super.setStackTrace(stackTrace);
        this.errorCode = dbmEx.getErrorCode();
        this.errorID = dbmEx.getErrorID();
    }

    public DBMConnectionException(RTEException rteEx, String msg, Throwable cause, StackTraceElement[] stackTrace) {
        super(msg, cause);
        super.setStackTrace(stackTrace);
        this.errorCode = rteEx.getDetailErrorCode();
        this.errorID = String.format("<Return code : %s>", rteEx.getRTEReturncode());
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorID() {
        return this.errorID;
    }
}
