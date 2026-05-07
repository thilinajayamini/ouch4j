package com.ouch.constant;

/**
 * OUCH 5.0 Time In Force values.
 *
 * In OUCH 5.0, TIF is represented as a 4-byte unsigned integer.
 * - 0 = Market Hours (Day order)
 * - 99999 = Immediate or Cancel (IOC)
 * - Other values represent the number of seconds until expiration
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public enum TimeInForce {

    /** Market Hours / Day order - valid for the current trading day */
    MARKET_HOURS(0),

    /** Immediate or Cancel - execute immediately or cancel */
    IOC(99999),

    /** System Hours - valid for the entire system session */
    SYSTEM_HOURS(99998),

    /** Good Till Extended Market Close */
    EXTENDED(99997);

    private final int value;

    TimeInForce(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Lookup TimeInForce by its OUCH integer value.
     */
    public static TimeInForce fromValue(int value) {
        for (TimeInForce tif : values()) {
            if (tif.value == value) {
                return tif;
            }
        }
        // For custom second-based TIF values, return null (use raw int)
        return null;
    }

    /**
     * Convert a FIX TimeInForce (tag 59) value to OUCH TIF integer.
     * FIX: 0=Day, 1=GTC, 3=IOC, 4=FOK, 6=GTD, 7=AtClose
     */
    public static int fromFixTimeInForce(char fixTif) {
        switch (fixTif) {
            case '0': return MARKET_HOURS.value;   // Day
            case '3': return IOC.value;            // IOC
            case '7': return EXTENDED.value;       // At Close → Extended
            default:
                throw new IllegalArgumentException("Unsupported FIX TimeInForce: " + fixTif);
        }
    }

    /**
     * Convert OUCH TIF value to FIX TimeInForce (tag 59) char.
     */
    public static char toFixTimeInForce(int ouchTif) {
        if (ouchTif == MARKET_HOURS.value) {
            return '0'; // Day
        } else if (ouchTif == IOC.value) {
            return '3'; // IOC
        } else if (ouchTif == EXTENDED.value) {
            return '7'; // At Close
        } else {
            return '0'; // Default to Day for custom TIF
        }
    }
}
