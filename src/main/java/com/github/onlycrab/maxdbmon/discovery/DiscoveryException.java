package com.github.onlycrab.maxdbmon.discovery;

/**
 * This exception is thrown when an error occurred during the discovery process.
 *
 * @author Roman Ryncovich
 * @version 0.9
 */
public class DiscoveryException extends Exception {
    public DiscoveryException(String message) {
        super(message);
    }

    public DiscoveryException(String message, Throwable cause, StackTraceElement[] stackTrace) {
        super(message, cause);
        super.setStackTrace(stackTrace);
    }
}
