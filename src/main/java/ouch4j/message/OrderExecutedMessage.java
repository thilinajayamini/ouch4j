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
 * OUCH 5.0 Order Executed Message (Type = 'E').
 *
 * Outbound message notifying that part or all of an order has been filled.
 *
 * Fixed size: 40 bytes (excluding appendages).
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class OrderExecutedMessage extends AbstractOuchMessage implements OutboundMessage {

    public static final int FIXED_SIZE = 40;

    private long timestamp;
    private String orderToken;
    private int executedShares;
    private int executionPrice;
    private byte liquidityFlag;
    private long matchNumber;

    @Override
    public byte getMessageType() {
        return MessageType.ORDER_EXECUTED;
    }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getOrderToken() { return orderToken; }
    public void setOrderToken(String orderToken) { this.orderToken = orderToken; }
    public int getExecutedShares() { return executedShares; }
    public void setExecutedShares(int executedShares) { this.executedShares = executedShares; }
    public int getExecutionPrice() { return executionPrice; }
    public void setExecutionPrice(int executionPrice) { this.executionPrice = executionPrice; }
    public byte getLiquidityFlag() { return liquidityFlag; }
    public void setLiquidityFlag(byte liquidityFlag) { this.liquidityFlag = liquidityFlag; }
    public long getMatchNumber() { return matchNumber; }
    public void setMatchNumber(long matchNumber) { this.matchNumber = matchNumber; }

    @Override
    protected String toStringFields() {
        return "timestamp=" + timestamp +
                ", orderToken='" + orderToken + '\'' +
                ", executedShares=" + executedShares +
                ", executionPrice=" + executionPrice +
                ", liquidity=" + (char) liquidityFlag +
                ", matchNumber=" + matchNumber;
    }
}
