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
 * OUCH 5.0 Enter Order Message (Type = 'O').
 *
 * Inbound message sent by the client to submit a new order to the exchange.
 *
 * Binary layout (fixed portion):
 * | Offset | Length | Field                      |
 * |--------|--------|----------------------------|
 * | 0      | 1      | Message Type ('O')         |
 * | 1      | 14     | Order Token                |
 * | 15     | 1      | Buy/Sell Indicator         |
 * | 16     | 4      | Shares                     |
 * | 20     | 8      | Stock                      |
 * | 28     | 4      | Price                      |
 * | 32     | 4      | Time in Force              |
 * | 36     | 1      | Display                    |
 * | 37     | 1      | Capacity                   |
 * | 38     | 1      | Intermarket Sweep Elig.    |
 * | 39     | 4      | Minimum Quantity            |
 * | 43     | 1      | Cross Type                 |
 * | 44     | 1      | Customer Type              |
 * | 45     | var    | Appendage (optional)       |
 *
 * Total fixed size: 45 bytes
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class EnterOrderMessage extends AbstractOuchMessage implements InboundMessage {

    /** Fixed portion size in bytes (excluding appendages) */
    public static final int FIXED_SIZE = 45;

    /** Token length in characters */
    public static final int ORDER_TOKEN_LENGTH = 14;

    /** Stock symbol length in characters */
    public static final int STOCK_LENGTH = 8;

    private String orderToken;         // 14 bytes alpha
    private byte buySellIndicator;     // 1 byte
    private int shares;                // 4 bytes unsigned int
    private String stock;              // 8 bytes alpha
    private int price;                 // 4 bytes unsigned int (scaled)
    private int timeInForce;           // 4 bytes unsigned int
    private byte display;              // 1 byte
    private byte capacity;             // 1 byte
    private byte intermarketSweep;     // 1 byte
    private int minimumQuantity;       // 4 bytes unsigned int
    private byte crossType;            // 1 byte
    private byte customerType;         // 1 byte

    @Override
    public byte getMessageType() {
        return MessageType.ENTER_ORDER;
    }

    // ========== Getters and Setters ==========

    public String getOrderToken() {
        return orderToken;
    }

    public void setOrderToken(String orderToken) {
        this.orderToken = orderToken;
    }

    public byte getBuySellIndicator() {
        return buySellIndicator;
    }

    public void setBuySellIndicator(byte buySellIndicator) {
        this.buySellIndicator = buySellIndicator;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
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

    public byte getCapacity() {
        return capacity;
    }

    public void setCapacity(byte capacity) {
        this.capacity = capacity;
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

    public byte getCrossType() {
        return crossType;
    }

    public void setCrossType(byte crossType) {
        this.crossType = crossType;
    }

    public byte getCustomerType() {
        return customerType;
    }

    public void setCustomerType(byte customerType) {
        this.customerType = customerType;
    }

    @Override
    protected String toStringFields() {
        return "orderToken='" + orderToken + '\'' +
                ", side=" + (char) buySellIndicator +
                ", shares=" + shares +
                ", stock='" + (stock != null ? stock.trim() : null) + '\'' +
                ", price=" + price +
                ", tif=" + timeInForce +
                ", display=" + (char) display +
                ", capacity=" + (char) capacity;
    }
}
