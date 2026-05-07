package com.ouch.constant;

/**
 * OUCH 5.0 System Event codes.
 * Returned in the System Event outbound message.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public enum EventCode {

    /** Start of Day - Market is opening */
    START_OF_DAY('S'),

    /** End of Day - Market is closing */
    END_OF_DAY('E');

    private final byte value;

    EventCode(char value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * Lookup EventCode by its OUCH byte value.
     */
    public static EventCode fromValue(byte value) {
        for (EventCode ec : values()) {
            if (ec.value == value) {
                return ec;
            }
        }
        return null;
    }
}
