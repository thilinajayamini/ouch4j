/*
 * Copyright (C) 2026 Thilina Jayamini
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ouch4j.constant;

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
