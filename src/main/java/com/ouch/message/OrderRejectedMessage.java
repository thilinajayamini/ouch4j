package com.ouch.message;

import com.ouch.constant.MessageType;

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
