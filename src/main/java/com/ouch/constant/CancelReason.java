package com.ouch.constant;

/**
 * OUCH 5.0 Cancel Reason codes.
 * Returned in the Order Canceled outbound message.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public enum CancelReason {

    /** User requested cancellation */
    USER_REQUESTED('U'),

    /** Immediate or Cancel (IOC) order timed out */
    IOC_TIMEOUT('I'),

    /** Timeout - order expired per its Time in Force */
    TIMEOUT('T'),

    /** Supervisory / system-initiated cancellation */
    SUPERVISORY('S'),

    /** Regulatory restriction */
    REGULATORY('D'),

    /** Self-match prevention */
    SELF_MATCH('Q'),

    /** Cross cancelled */
    CROSS('C');

    private final byte value;

    CancelReason(char value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * Lookup CancelReason by its OUCH byte value.
     */
    public static CancelReason fromValue(byte value) {
        for (CancelReason reason : values()) {
            if (reason.value == value) {
                return reason;
            }
        }
        return null; // Unknown reason — caller should handle gracefully
    }
}
