package com.github.onlycrab.maxdbmon.extractor.state;

/**
 * List of possible MaxDB power states.
 * <blockquote>
 * OFFLINE(0)   - database instance is not running,
 * ONLINE(1)    - database instance is ready,
 * ADMIN(2)     - database is only available for administration tasks,
 * STANDBY(3)   - for standby instances in hot standby systems only (it is in a constant restart state),
 * UNKNOWN(4)   - database state is unknown.
 * </blockquote>
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
@SuppressWarnings("ALL")
public enum OperationalState {
    OFFLINE(0),
    ONLINE(1),
    ADMIN(2),
    STANDBY(3),
    UNKNOWN(4);

    private final int code;

    OperationalState(int code) {
        this.code = code;
    }

    /**
     * Returns operational(power) state numeric code.
     *
     * @return operational(power) state numeric code
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns operational state by code.
     * @param code integer representation of operational state
     * @return operational state by code
     */
    public static OperationalState fromCode(int code){
        switch (code){
            case 0:
                return OFFLINE;
            case 1:
                return ONLINE;
            case 2:
                return ADMIN;
            case 3:
                return STANDBY;
            default:
                return UNKNOWN;
        }
    }
}
