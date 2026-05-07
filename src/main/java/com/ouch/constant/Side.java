package com.ouch.constant;

/**
 * OUCH 5.0 Buy/Sell side indicators.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public enum Side {

    /** Buy order */
    BUY('B'),

    /** Sell order */
    SELL('S'),

    /** Sell Short */
    SELL_SHORT('T'),

    /** Sell Short Exempt */
    SELL_SHORT_EXEMPT('E');

    private final byte value;

    Side(char value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * Lookup Side by its OUCH byte value.
     *
     * @throws IllegalArgumentException if the value is not a valid side
     */
    public static Side fromValue(byte value) {
        for (Side side : values()) {
            if (side.value == value) {
                return side;
            }
        }
        throw new IllegalArgumentException("Unknown side value: " + (char) value);
    }

    /**
     * Convert a FIX Side (tag 54) value to OUCH Side.
     * FIX: 1=Buy, 2=Sell, 5=SellShort, 6=SellShortExempt
     */
    public static Side fromFixSide(char fixSide) {
        switch (fixSide) {
            case '1': return BUY;
            case '2': return SELL;
            case '5': return SELL_SHORT;
            case '6': return SELL_SHORT_EXEMPT;
            default:
                throw new IllegalArgumentException("Unsupported FIX side: " + fixSide);
        }
    }

    /**
     * Convert this OUCH Side to FIX Side (tag 54) value.
     */
    public char toFixSide() {
        switch (this) {
            case BUY:              return '1';
            case SELL:             return '2';
            case SELL_SHORT:       return '5';
            case SELL_SHORT_EXEMPT: return '6';
            default:
                throw new IllegalStateException("Unmapped side: " + this);
        }
    }
}
