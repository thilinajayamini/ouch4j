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

package ouch4j.codec;

import ouch4j.constant.MessageType;
import ouch4j.message.*;
import ouch4j.message.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import static ouch4j.codec.ByteBufferUtil.*;

/**
 * OUCH 5.0 Message Decoder.
 *
 * Decodes binary ByteBuffer data into OUCH message POJOs.
 * Supports both inbound and outbound message types.
 *
 * The decoder is forward-compatible: unknown appendage tags are
 * preserved as raw byte arrays and unknown message types result
 * in an exception rather than silent data corruption.
 *
 * Thread-safety: This class is stateless and thread-safe.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class OuchDecoder {

    private static final int ORDER_TOKEN_LEN = 14;
    private static final int STOCK_LEN = 8;

    /**
     * Decodes a ByteBuffer into the appropriate OUCH message POJO.
     *
     * @param buffer the buffer to decode (position should be at the start of the
     *               message)
     * @return the decoded OUCH message
     * @throws IllegalArgumentException if the message type is unknown
     */
    public OuchMessage decode(ByteBuffer buffer) {
        buffer.order(ByteOrder.BIG_ENDIAN);
        byte type = buffer.get(buffer.position()); // Peek at type without advancing

        switch (type) {
            // Inbound messages
            case MessageType.ENTER_ORDER:
                return decodeEnterOrder(buffer);
            case MessageType.REPLACE_ORDER:
                return decodeReplaceOrder(buffer);
            case MessageType.CANCEL_ORDER:
                return decodeCancelOrder(buffer);

            // Outbound messages
            case MessageType.SYSTEM_EVENT:
                return decodeSystemEvent(buffer);
            case MessageType.ORDER_ACCEPTED:
                return decodeOrderAccepted(buffer);
            case MessageType.ORDER_REPLACED:
                return decodeOrderReplaced(buffer);
            case MessageType.ORDER_EXECUTED:
                return decodeOrderExecuted(buffer);
            case MessageType.ORDER_CANCELED:
                return decodeOrderCanceled(buffer);
            case MessageType.ORDER_REJECTED:
                return decodeOrderRejected(buffer);

            default:
                throw new IllegalArgumentException(
                        "Unknown message type: 0x" + Integer.toHexString(type & 0xFF)
                                + " ('" + (char) type + "')");
        }
    }

    // ==================== Inbound Decoders ====================

    public EnterOrderMessage decodeEnterOrder(ByteBuffer buf) {
        EnterOrderMessage msg = new EnterOrderMessage();
        readByte(buf); // skip message type
        msg.setOrderToken(readAlpha(buf, ORDER_TOKEN_LEN));
        msg.setBuySellIndicator(readByte(buf));
        msg.setShares(readInt(buf));
        msg.setStock(readAlpha(buf, STOCK_LEN));
        msg.setPrice(readInt(buf));
        msg.setTimeInForce(readInt(buf));
        msg.setDisplay(readByte(buf));
        msg.setCapacity(readByte(buf));
        msg.setIntermarketSweep(readByte(buf));
        msg.setMinimumQuantity(readInt(buf));
        msg.setCrossType(readByte(buf));
        msg.setCustomerType(readByte(buf));
        readAppendages(buf, msg);
        return msg;
    }

    public ReplaceOrderMessage decodeReplaceOrder(ByteBuffer buf) {
        ReplaceOrderMessage msg = new ReplaceOrderMessage();
        readByte(buf); // skip message type
        msg.setExistingOrderToken(readAlpha(buf, ORDER_TOKEN_LEN));
        msg.setReplacementOrderToken(readAlpha(buf, ORDER_TOKEN_LEN));
        msg.setShares(readInt(buf));
        msg.setPrice(readInt(buf));
        msg.setTimeInForce(readInt(buf));
        msg.setDisplay(readByte(buf));
        msg.setIntermarketSweep(readByte(buf));
        msg.setMinimumQuantity(readInt(buf));
        readAppendages(buf, msg);
        return msg;
    }

    public CancelOrderMessage decodeCancelOrder(ByteBuffer buf) {
        CancelOrderMessage msg = new CancelOrderMessage();
        readByte(buf); // skip message type
        msg.setOrderToken(readAlpha(buf, ORDER_TOKEN_LEN));
        msg.setShares(readInt(buf));
        readAppendages(buf, msg);
        return msg;
    }

    // ==================== Outbound Decoders ====================

    public SystemEventMessage decodeSystemEvent(ByteBuffer buf) {
        SystemEventMessage msg = new SystemEventMessage();
        readByte(buf); // skip message type
        msg.setTimestamp(readLong(buf));
        msg.setEventCode(readByte(buf));
        return msg;
    }

    public OrderAcceptedMessage decodeOrderAccepted(ByteBuffer buf) {
        OrderAcceptedMessage msg = new OrderAcceptedMessage();
        readByte(buf); // skip message type
        msg.setTimestamp(readLong(buf));
        msg.setOrderToken(readAlpha(buf, ORDER_TOKEN_LEN));
        msg.setBuySellIndicator(readByte(buf));
        msg.setShares(readInt(buf));
        msg.setStock(readAlpha(buf, STOCK_LEN));
        msg.setPrice(readInt(buf));
        msg.setTimeInForce(readInt(buf));
        msg.setOrderReferenceNumber(readLong(buf));
        msg.setDisplay(readByte(buf));
        msg.setCapacity(readByte(buf));
        msg.setIntermarketSweep(readByte(buf));
        msg.setMinimumQuantity(readInt(buf));
        msg.setCrossType(readByte(buf));
        msg.setOrderState(readByte(buf));
        readAppendages(buf, msg);
        return msg;
    }

    public OrderReplacedMessage decodeOrderReplaced(ByteBuffer buf) {
        OrderReplacedMessage msg = new OrderReplacedMessage();
        readByte(buf); // skip message type
        msg.setTimestamp(readLong(buf));
        msg.setReplacementOrderToken(readAlpha(buf, ORDER_TOKEN_LEN));
        msg.setBuySellIndicator(readByte(buf));
        msg.setShares(readInt(buf));
        msg.setStock(readAlpha(buf, STOCK_LEN));
        msg.setPrice(readInt(buf));
        msg.setTimeInForce(readInt(buf));
        msg.setOrderReferenceNumber(readLong(buf));
        msg.setDisplay(readByte(buf));
        msg.setCapacity(readByte(buf));
        msg.setIntermarketSweep(readByte(buf));
        msg.setMinimumQuantity(readInt(buf));
        msg.setCrossType(readByte(buf));
        msg.setOrderState(readByte(buf));
        // Previous order token may be in appendages
        readAppendages(buf, msg);
        return msg;
    }

    public OrderExecutedMessage decodeOrderExecuted(ByteBuffer buf) {
        OrderExecutedMessage msg = new OrderExecutedMessage();
        readByte(buf); // skip message type
        msg.setTimestamp(readLong(buf));
        msg.setOrderToken(readAlpha(buf, ORDER_TOKEN_LEN));
        msg.setExecutedShares(readInt(buf));
        msg.setExecutionPrice(readInt(buf));
        msg.setLiquidityFlag(readByte(buf));
        msg.setMatchNumber(readLong(buf));
        readAppendages(buf, msg);
        return msg;
    }

    public OrderCanceledMessage decodeOrderCanceled(ByteBuffer buf) {
        OrderCanceledMessage msg = new OrderCanceledMessage();
        readByte(buf); // skip message type
        msg.setTimestamp(readLong(buf));
        msg.setOrderToken(readAlpha(buf, ORDER_TOKEN_LEN));
        msg.setDecrementShares(readInt(buf));
        msg.setReason(readByte(buf));
        readAppendages(buf, msg);
        return msg;
    }

    public OrderRejectedMessage decodeOrderRejected(ByteBuffer buf) {
        OrderRejectedMessage msg = new OrderRejectedMessage();
        readByte(buf); // skip message type
        msg.setTimestamp(readLong(buf));
        msg.setOrderToken(readAlpha(buf, ORDER_TOKEN_LEN));
        msg.setRejectReasonCode(readUnsignedShort(buf));
        readAppendages(buf, msg);
        return msg;
    }

    // ==================== Appendage Parsing ====================

    /**
     * Reads TagValue appendages from the remaining bytes in the buffer.
     * Unknown tags are preserved as raw byte arrays for forward compatibility.
     */
    private void readAppendages(ByteBuffer buf, OuchMessage msg) {
        while (buf.remaining() >= 4) { // Minimum: 2 (tag) + 2 (length)
            int tagId = readUnsignedShort(buf);
            int length = readUnsignedShort(buf);

            if (buf.remaining() < length) {
                break; // Malformed appendage — not enough data
            }

            byte[] value = new byte[length];
            buf.get(value);
            msg.addAppendage(tagId, value);
        }
    }
}
