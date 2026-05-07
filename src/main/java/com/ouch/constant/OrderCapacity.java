package com.ouch.constant;

/**
 * OUCH 5.0 Order Capacity indicators.
 * Identifies whether the order is entered in an agency or principal capacity.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public enum OrderCapacity {

    /** Agency - acting on behalf of a customer */
    AGENCY('A'),

    /** Principal - trading for the firm's own account */
    PRINCIPAL('P'),

    /** Riskless Principal */
    RISKLESS('R');

    private final byte value;

    OrderCapacity(char value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * Lookup OrderCapacity by its OUCH byte value.
     */
    public static OrderCapacity fromValue(byte value) {
        for (OrderCapacity cap : values()) {
            if (cap.value == value) {
                return cap;
            }
        }
        throw new IllegalArgumentException("Unknown capacity value: " + (char) value);
    }
}
