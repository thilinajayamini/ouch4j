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
