package com.ouch.constant;

/**
 * OUCH 5.0 Intermarket Sweep Eligibility indicators.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public enum IntermarketSweep {

    /** Eligible for Intermarket Sweep */
    ELIGIBLE('Y'),

    /** Not eligible for Intermarket Sweep */
    NOT_ELIGIBLE('N'),

    /** Eligible - but this is an ISO order that routes to other markets */
    ELIGIBLE_ISO('y');

    private final byte value;

    IntermarketSweep(char value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * Lookup IntermarketSweep by its OUCH byte value.
     */
    public static IntermarketSweep fromValue(byte value) {
        for (IntermarketSweep ims : values()) {
            if (ims.value == value) {
                return ims;
            }
        }
        throw new IllegalArgumentException("Unknown intermarket sweep value: " + (char) value);
    }
}
