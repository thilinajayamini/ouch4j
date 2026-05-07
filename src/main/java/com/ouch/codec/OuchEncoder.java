package com.ouch.codec;

import com.ouch.constant.MessageType;
import com.ouch.message.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

import static com.ouch.codec.ByteBufferUtil.*;

/**
 * OUCH 5.0 Message Encoder.
 *
 * Encodes OUCH message POJOs into binary ByteBuffer representations
 * suitable for transmission over SoupBinTCP.
 *
 * Thread-safety: This class is stateless and thread-safe.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class OuchEncoder {

    private static final int ORDER_TOKEN_LEN = 14;
    private static final int STOCK_LEN = 8;

    /**
     * Encodes any OUCH message to a ByteBuffer.
     *
     * @param message the OUCH message to encode
     * @return a ByteBuffer positioned at 0, with limit set to the message length
     * @throws IllegalArgumentException if the message type is not supported
     */
    public ByteBuffer encode(OuchMessage message) {
        byte type = message.getMessageType();
        switch (type) {
            case MessageType.ENTER_ORDER:
                return encodeEnterOrder((EnterOrderMessage) message);
            case MessageType.REPLACE_ORDER:
                return encodeReplaceOrder((ReplaceOrderMessage) message);
            case MessageType.CANCEL_ORDER:
                return encodeCancelOrder((CancelOrderMessage) message);
            default:
                throw new IllegalArgumentException(
                        "Encoding not supported for message type: " + MessageType.name(type));
        }
    }

    /**
     * Encodes an Enter Order message ('O').
     */
    public ByteBuffer encodeEnterOrder(EnterOrderMessage msg) {
        int appendageLen = ((AbstractOuchMessage) msg).getAppendagesLength();
        ByteBuffer buf = allocate(EnterOrderMessage.FIXED_SIZE + appendageLen);

        writeByte(buf, MessageType.ENTER_ORDER);
        writeAlpha(buf, msg.getOrderToken(), ORDER_TOKEN_LEN);
        writeByte(buf, msg.getBuySellIndicator());
        writeInt(buf, msg.getShares());
        writeAlpha(buf, msg.getStock(), STOCK_LEN);
        writeInt(buf, msg.getPrice());
        writeInt(buf, msg.getTimeInForce());
        writeByte(buf, msg.getDisplay());
        writeByte(buf, msg.getCapacity());
        writeByte(buf, msg.getIntermarketSweep());
        writeInt(buf, msg.getMinimumQuantity());
        writeByte(buf, msg.getCrossType());
        writeByte(buf, msg.getCustomerType());

        writeAppendages(buf, msg);
        buf.flip();
        return buf;
    }

    /**
     * Encodes a Replace Order message ('U').
     */
    public ByteBuffer encodeReplaceOrder(ReplaceOrderMessage msg) {
        int appendageLen = ((AbstractOuchMessage) msg).getAppendagesLength();
        ByteBuffer buf = allocate(ReplaceOrderMessage.FIXED_SIZE + appendageLen);

        writeByte(buf, MessageType.REPLACE_ORDER);
        writeAlpha(buf, msg.getExistingOrderToken(), ORDER_TOKEN_LEN);
        writeAlpha(buf, msg.getReplacementOrderToken(), ORDER_TOKEN_LEN);
        writeInt(buf, msg.getShares());
        writeInt(buf, msg.getPrice());
        writeInt(buf, msg.getTimeInForce());
        writeByte(buf, msg.getDisplay());
        writeByte(buf, msg.getIntermarketSweep());
        writeInt(buf, msg.getMinimumQuantity());

        writeAppendages(buf, msg);
        buf.flip();
        return buf;
    }

    /**
     * Encodes a Cancel Order message ('X').
     */
    public ByteBuffer encodeCancelOrder(CancelOrderMessage msg) {
        int appendageLen = ((AbstractOuchMessage) msg).getAppendagesLength();
        ByteBuffer buf = allocate(CancelOrderMessage.FIXED_SIZE + appendageLen);

        writeByte(buf, MessageType.CANCEL_ORDER);
        writeAlpha(buf, msg.getOrderToken(), ORDER_TOKEN_LEN);
        writeInt(buf, msg.getShares());

        writeAppendages(buf, msg);
        buf.flip();
        return buf;
    }

    // ==================== Helper Methods ====================

    /**
     * Allocates a ByteBuffer with big-endian byte order.
     */
    private ByteBuffer allocate(int size) {
        return ByteBuffer.allocate(size).order(ByteOrder.BIG_ENDIAN);
    }

    /**
     * Writes all TagValue appendages to the buffer.
     */
    private void writeAppendages(ByteBuffer buf, OuchMessage msg) {
        for (Map.Entry<Integer, byte[]> entry : msg.getAppendages().entrySet()) {
            writeUnsignedShort(buf, entry.getKey());
            writeUnsignedShort(buf, entry.getValue().length);
            buf.put(entry.getValue());
        }
    }
}
