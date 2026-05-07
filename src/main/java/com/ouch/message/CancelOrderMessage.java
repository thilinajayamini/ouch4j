package com.ouch.message;

import com.ouch.constant.MessageType;

/**
 * OUCH 5.0 Cancel Order Message (Type = 'X').
 *
 * Inbound message sent by the client to cancel or reduce quantity of a live order.
 * Set shares to 0 to cancel the entire order.
 *
 * Binary layout (fixed portion):
 * | Offset | Length | Field                      |
 * |--------|--------|----------------------------|
 * | 0      | 1      | Message Type ('X')         |
 * | 1      | 14     | Order Token                |
 * | 15     | 4      | Shares                     |
 * | 19     | var    | Appendage (optional)       |
 *
 * Total fixed size: 19 bytes
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class CancelOrderMessage extends AbstractOuchMessage implements InboundMessage {

    /** Fixed portion size in bytes (excluding appendages) */
    public static final int FIXED_SIZE = 19;

    private String orderToken;    // 14 bytes alpha
    private int shares;           // 4 bytes unsigned int (0 = cancel all)

    @Override
    public byte getMessageType() {
        return MessageType.CANCEL_ORDER;
    }

    // ========== Getters and Setters ==========

    public String getOrderToken() {
        return orderToken;
    }

    public void setOrderToken(String orderToken) {
        this.orderToken = orderToken;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    @Override
    protected String toStringFields() {
        return "orderToken='" + orderToken + '\'' +
                ", shares=" + shares;
    }
}
