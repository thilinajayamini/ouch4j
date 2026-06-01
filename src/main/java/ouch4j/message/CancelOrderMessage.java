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
