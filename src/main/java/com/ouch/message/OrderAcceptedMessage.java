package com.ouch.message;

import com.ouch.constant.MessageType;

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
