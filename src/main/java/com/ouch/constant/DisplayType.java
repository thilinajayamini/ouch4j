package com.ouch.constant;

/**
 * OUCH 5.0 Display type indicators.
 * Controls the visibility of the order on the exchange book.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public enum DisplayType {

    /** Attributable - Price to display */
    ATTRIBUTABLE_PRICE('A'),

    /** Anonymous - Price to display (default) */
    ANONYMOUS('Y'),

    /** Non-displayed */
    NON_DISPLAYED('N'),

    /** Post-only */
    POST_ONLY('P'),

    /** Imbalance-only */
    IMBALANCE_ONLY('I');

    private final byte value;

    DisplayType(char value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * Lookup DisplayType by its OUCH byte value.
     */
    public static DisplayType fromValue(byte value) {
        for (DisplayType dt : values()) {
            if (dt.value == value) {
                return dt;
            }
        }
        throw new IllegalArgumentException("Unknown display type value: " + (char) value);
    }
}
