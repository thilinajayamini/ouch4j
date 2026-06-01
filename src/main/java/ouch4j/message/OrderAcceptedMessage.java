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
 * OUCH 5.0 Order Accepted Message (Type = 'A').
 *
 * Outbound message confirming a new order was accepted.
 * Fixed size: 61 bytes (excluding appendages).
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class OrderAcceptedMessage extends AbstractOuchMessage implements OutboundMessage {

    public static final int FIXED_SIZE = 61;

    private long timestamp;
    private String orderToken;
    private byte buySellIndicator;
    private int shares;
    private String stock;
    private int price;
    private int timeInForce;
    private long orderReferenceNumber;
    private byte display;
    private byte capacity;
    private byte intermarketSweep;
    private int minimumQuantity;
    private byte crossType;
    private byte orderState;

    @Override
    public byte getMessageType() {
        return MessageType.ORDER_ACCEPTED;
    }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getOrderToken() { return orderToken; }
    public void setOrderToken(String orderToken) { this.orderToken = orderToken; }
    public byte getBuySellIndicator() { return buySellIndicator; }
    public void setBuySellIndicator(byte buySellIndicator) { this.buySellIndicator = buySellIndicator; }
    public int getShares() { return shares; }
    public void setShares(int shares) { this.shares = shares; }
    public String getStock() { return stock; }
    public void setStock(String stock) { this.stock = stock; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public int getTimeInForce() { return timeInForce; }
    public void setTimeInForce(int timeInForce) { this.timeInForce = timeInForce; }
    public long getOrderReferenceNumber() { return orderReferenceNumber; }
    public void setOrderReferenceNumber(long orderReferenceNumber) { this.orderReferenceNumber = orderReferenceNumber; }
    public byte getDisplay() { return display; }
    public void setDisplay(byte display) { this.display = display; }
    public byte getCapacity() { return capacity; }
    public void setCapacity(byte capacity) { this.capacity = capacity; }
    public byte getIntermarketSweep() { return intermarketSweep; }
    public void setIntermarketSweep(byte intermarketSweep) { this.intermarketSweep = intermarketSweep; }
    public int getMinimumQuantity() { return minimumQuantity; }
    public void setMinimumQuantity(int minimumQuantity) { this.minimumQuantity = minimumQuantity; }
    public byte getCrossType() { return crossType; }
    public void setCrossType(byte crossType) { this.crossType = crossType; }
    public byte getOrderState() { return orderState; }
    public void setOrderState(byte orderState) { this.orderState = orderState; }

    @Override
    protected String toStringFields() {
        return "timestamp=" + timestamp +
                ", orderToken='" + orderToken + '\'' +
                ", side=" + (char) buySellIndicator +
                ", shares=" + shares +
                ", stock='" + (stock != null ? stock.trim() : null) + '\'' +
                ", price=" + price +
                ", orderRef=" + orderReferenceNumber +
                ", state=" + (char) orderState;
    }
}
