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
