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

package ouch4j.message;

import ouch4j.constant.MessageType;

/**
 * OUCH 5.0 Order Canceled Message (Type = 'C').
 *
 * Outbound message notifying that an order has been canceled.
 * Fixed size: 28 bytes (excluding appendages).
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class OrderCanceledMessage extends AbstractOuchMessage implements OutboundMessage {

    public static final int FIXED_SIZE = 28;

    private long timestamp;
    private String orderToken;
    private int decrementShares;
    private byte reason;

    @Override
    public byte getMessageType() {
        return MessageType.ORDER_CANCELED;
    }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getOrderToken() { return orderToken; }
    public void setOrderToken(String orderToken) { this.orderToken = orderToken; }
    public int getDecrementShares() { return decrementShares; }
    public void setDecrementShares(int decrementShares) { this.decrementShares = decrementShares; }
    public byte getReason() { return reason; }
    public void setReason(byte reason) { this.reason = reason; }

    @Override
    protected String toStringFields() {
        return "timestamp=" + timestamp +
                ", orderToken='" + orderToken + '\'' +
                ", decrementShares=" + decrementShares +
                ", reason=" + (char) reason;
    }
}
