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
 * OUCH 5.0 Order Rejected Message (Type = 'J').
 *
 * Outbound message notifying that an Enter or Replace order was rejected.
 * Fixed size: 25 bytes (excluding appendages).
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class OrderRejectedMessage extends AbstractOuchMessage implements OutboundMessage {

    public static final int FIXED_SIZE = 25;

    private long timestamp;
    private String orderToken;
    private int rejectReasonCode;

    @Override
    public byte getMessageType() {
        return MessageType.ORDER_REJECTED;
    }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getOrderToken() { return orderToken; }
    public void setOrderToken(String orderToken) { this.orderToken = orderToken; }
    public int getRejectReasonCode() { return rejectReasonCode; }
    public void setRejectReasonCode(int code) { this.rejectReasonCode = code; }

    @Override
    protected String toStringFields() {
        return "timestamp=" + timestamp +
                ", orderToken='" + orderToken + '\'' +
                ", rejectReason=" + rejectReasonCode;
    }
}
