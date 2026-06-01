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
 * OUCH 5.0 System Event codes.
 * Returned in the System Event outbound message.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public enum EventCode {

    /** Start of Day - Market is opening */
    START_OF_DAY('S'),

    /** End of Day - Market is closing */
    END_OF_DAY('E');

    private final byte value;

    EventCode(char value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * Lookup EventCode by its OUCH byte value.
     */
    public static EventCode fromValue(byte value) {
        for (EventCode ec : values()) {
            if (ec.value == value) {
                return ec;
            }
        }
        return null;
    }
}
