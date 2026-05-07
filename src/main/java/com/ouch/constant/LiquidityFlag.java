package com.ouch.constant;

/**
 * OUCH 5.0 Liquidity Flag indicators.
 * Returned in the Order Executed outbound message to indicate
 * whether the order added or removed liquidity.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public enum LiquidityFlag {

    /** Added liquidity (passive fill - was resting on the book) */
    ADDED('A'),

    /** Removed liquidity (aggressive fill - took from the book) */
    REMOVED('R'),

    /** Opening cross */
    OPENING('O'),

    /** Closing cross */
    CLOSING('C'),

    /** Halt/IPO cross */
    HALT_CROSS('H'),

    /** Supplemental order execution */
    SUPPLEMENTAL('S'),

    /** Retail designated */
    RETAIL('l');

    private final byte value;

    LiquidityFlag(char value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * Lookup LiquidityFlag by its OUCH byte value.
     */
    public static LiquidityFlag fromValue(byte value) {
        for (LiquidityFlag lf : values()) {
            if (lf.value == value) {
                return lf;
            }
        }
        return null; // Unknown flag — caller should handle gracefully
    }
}
