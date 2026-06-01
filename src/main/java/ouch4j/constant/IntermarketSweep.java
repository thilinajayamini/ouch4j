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
