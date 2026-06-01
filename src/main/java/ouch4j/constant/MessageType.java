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
 * OUCH 5.0 Message Type identifiers.
 *
 * Inbound messages are sent from the client to the exchange.
 * Outbound messages are sent from the exchange to the client.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public final class MessageType {

    private MessageType() {
        // Utility class
    }

    // ========== Inbound (Client → Exchange) ==========

    /** Enter Order - Submit a new order */
    public static final byte ENTER_ORDER = (byte) 'O';

    /** Replace Order - Modify an existing order (price, qty, etc.) */
    public static final byte REPLACE_ORDER = (byte) 'U';

    /** Cancel Order - Cancel or reduce quantity of a live order */
    public static final byte CANCEL_ORDER = (byte) 'X';

    // ========== Outbound (Exchange → Client) ==========

    /** System Event - System-wide notifications (start/end of day) */
    public static final byte SYSTEM_EVENT = (byte) 'S';

    /** Order Accepted - Confirms an Enter Order was accepted */
    public static final byte ORDER_ACCEPTED = (byte) 'A';

    /** Order Replaced - Confirms a Replace Order was processed */
    public static final byte ORDER_REPLACED = (byte) 'R';

    /** Order Executed - Partial or full fill notification */
    public static final byte ORDER_EXECUTED = (byte) 'E';

    /** Order Canceled - Order was canceled (user or system) */
    public static final byte ORDER_CANCELED = (byte) 'C';

    /** Order Rejected - Enter or Replace was rejected */
    public static final byte ORDER_REJECTED = (byte) 'J';

    /**
     * Returns a human-readable name for the given message type.
     */
    public static String name(byte type) {
        switch (type) {
            case ENTER_ORDER:    return "EnterOrder";
            case REPLACE_ORDER:  return "ReplaceOrder";
            case CANCEL_ORDER:   return "CancelOrder";
            case SYSTEM_EVENT:   return "SystemEvent";
            case ORDER_ACCEPTED: return "OrderAccepted";
            case ORDER_REPLACED: return "OrderReplaced";
            case ORDER_EXECUTED: return "OrderExecuted";
            case ORDER_CANCELED: return "OrderCanceled";
            case ORDER_REJECTED: return "OrderRejected";
            default:             return "Unknown(0x" + Integer.toHexString(type & 0xFF) + ")";
        }
    }

    /**
     * Checks if the given type is an inbound (client→exchange) message.
     */
    public static boolean isInbound(byte type) {
        return type == ENTER_ORDER || type == REPLACE_ORDER || type == CANCEL_ORDER;
    }

    /**
     * Checks if the given type is an outbound (exchange→client) message.
     */
    public static boolean isOutbound(byte type) {
        return type == SYSTEM_EVENT || type == ORDER_ACCEPTED || type == ORDER_REPLACED
                || type == ORDER_EXECUTED || type == ORDER_CANCELED || type == ORDER_REJECTED;
    }
}
