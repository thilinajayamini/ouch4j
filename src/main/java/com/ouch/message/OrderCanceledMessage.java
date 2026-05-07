package com.ouch.message;

import com.ouch.constant.MessageType;

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
