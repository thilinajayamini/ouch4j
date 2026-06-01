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
 * OUCH 5.0 Cancel Reason codes.
 * Returned in the Order Canceled outbound message.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public enum CancelReason {

    /** User requested cancellation */
    USER_REQUESTED('U'),

    /** Immediate or Cancel (IOC) order timed out */
    IOC_TIMEOUT('I'),

    /** Timeout - order expired per its Time in Force */
    TIMEOUT('T'),

    /** Supervisory / system-initiated cancellation */
    SUPERVISORY('S'),

    /** Regulatory restriction */
    REGULATORY('D'),

    /** Self-match prevention */
    SELF_MATCH('Q'),

    /** Cross cancelled */
    CROSS('C');

    private final byte value;

    CancelReason(char value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * Lookup CancelReason by its OUCH byte value.
     */
    public static CancelReason fromValue(byte value) {
        for (CancelReason reason : values()) {
            if (reason.value == value) {
                return reason;
            }
        }
        return null; // Unknown reason — caller should handle gracefully
    }
}
