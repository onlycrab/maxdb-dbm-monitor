package com.github.onlycrab.maxdbmon.xserver;

/**
 * This exception is thrown when an error occurred while checking connection to XServer.
 *
 * @author Roman Ryncovich
 * @version 0.9
 */
@SuppressWarnings("WeakerAccess")
public class CheckSocketException extends Exception {
    CheckSocketException(String message) {
        super(message);
    }

    CheckSocketException(String message, Throwable cause, StackTraceElement[] stackTrace) {
        super(message, cause);
        super.setStackTrace(stackTrace);
    }
}
