package com.github.onlycrab.common;

/**
 * OS utilities.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class OS {
    /**
     * Returns OS type.
     *
     * @param throwError whether to throw exceptions in case of errors
     * @return OS type.
     * @throws RuntimeException if an exception occurred while getting the system value {@code os.name}.
     */
    public static OSType getOSType(boolean throwError) throws RuntimeException {
        String OS = getSystemValue("os.name", throwError).toLowerCase();
        if (OS.contains("windows")) {
            return OSType.WINDOWS;
        } else if (OS.contains("nix")) {
            return OSType.UNIX;
        } else if (OS.contains("nux")) {
            return OSType.LINUX;
        } else {
            return OSType.UNKNOWN;
        }
    }

    /**
     * Returns system value by key.
     * <p>First, an attempt is made to get a system property by the key. If property is not defined - try to get
     * environment variable by the same key.</p>
     *
     * @param key        name of system value
     * @param throwError whether to throw exceptions in case of errors
     * @return system value by key or empty string if value is not defined
     * @throws RuntimeException if key is {@code null};
     *                          if value is not defined;
     *                          if an system exception occurred while getting value.
     */
    public static String getSystemValue(String key, boolean throwError) throws RuntimeException {
        if (key == null) {
            if (throwError) {
                throw new RuntimeException("There was an error getting the value of an environment variable : key is null.");
            } else {
                return "";
            }
        }
        String result;
        try {
            result = System.getProperty(key);
            if (result == null) {
                result = System.getenv(key);
                if (result == null) {
                    if (throwError) {
                        throw new RuntimeException(String.format("Environment variable <%s> are <null>.", key));
                    } else {
                        return "";
                    }
                } else {
                    return result;
                }
            } else {
                return result;
            }
        } catch (SecurityException | IllegalArgumentException e) {
            if (throwError) {
                throw new RuntimeException(String.format("There was an error getting the value of an environment variable <%s> : %s.",
                        key, e.getMessage()));
            } else {
                return "";
            }
        }
    }

    /**
     * OS available types.
     */
    public enum OSType {
        WINDOWS("WINDOWS"),
        LINUX("LINUX"),
        UNIX("UNIX"),
        UNKNOWN("UNKNOWN");

        /**
         * String representation of OS type.
         */
        private final String code;

        OSType(String code) {
            this.code = code;
        }

        /**
         * Returns OS string code.
         *
         * @return OS string code.
         */
        @SuppressWarnings("unused")
        public String getCode() {
            return code;
        }
    }
}
