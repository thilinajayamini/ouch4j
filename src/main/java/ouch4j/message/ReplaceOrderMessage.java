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
 * OUCH 5.0 Replace Order Message (Type = 'U').
 *
 * Inbound message sent by the client to modify an existing live order.
 * This atomically cancels the old order and creates a replacement.
 *
 * Binary layout (fixed portion):
 * | Offset | Length | Field                      |
 * |--------|--------|----------------------------|
 * | 0      | 1      | Message Type ('U')         |
 * | 1      | 14     | Existing Order Token       |
 * | 15     | 14     | Replacement Order Token    |
 * | 29     | 4      | Shares                     |
 * | 33     | 4      | Price                      |
 * | 37     | 4      | Time in Force              |
 * | 41     | 1      | Display                    |
 * | 42     | 1      | Intermarket Sweep Elig.    |
 * | 43     | 4      | Minimum Quantity            |
 * | 47     | var    | Appendage (optional)       |
 *
 * Total fixed size: 47 bytes
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class ReplaceOrderMessage extends AbstractOuchMessage implements InboundMessage {

    /** Fixed portion size in bytes (excluding appendages) */
    public static final int FIXED_SIZE = 47;

    private String existingOrderToken;       // 14 bytes alpha
    private String replacementOrderToken;    // 14 bytes alpha
    private int shares;                      // 4 bytes unsigned int
    private int price;                       // 4 bytes unsigned int (scaled)
    private int timeInForce;                 // 4 bytes unsigned int
    private byte display;                    // 1 byte
    private byte intermarketSweep;           // 1 byte
    private int minimumQuantity;             // 4 bytes unsigned int

    @Override
    public byte getMessageType() {
        return MessageType.REPLACE_ORDER;
    }

    // ========== Getters and Setters ==========

    public String getExistingOrderToken() {
        return existingOrderToken;
    }

    public void setExistingOrderToken(String existingOrderToken) {
        this.existingOrderToken = existingOrderToken;
    }

    public String getReplacementOrderToken() {
        return replacementOrderToken;
    }

    public void setReplacementOrderToken(String replacementOrderToken) {
        this.replacementOrderToken = replacementOrderToken;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(int timeInForce) {
        this.timeInForce = timeInForce;
    }

    public byte getDisplay() {
        return display;
    }

    public void setDisplay(byte display) {
        this.display = display;
    }

    public byte getIntermarketSweep() {
        return intermarketSweep;
    }

    public void setIntermarketSweep(byte intermarketSweep) {
        this.intermarketSweep = intermarketSweep;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    @Override
    protected String toStringFields() {
        return "existingToken='" + existingOrderToken + '\'' +
                ", replacementToken='" + replacementOrderToken + '\'' +
                ", shares=" + shares +
                ", price=" + price +
                ", tif=" + timeInForce;
    }
}
