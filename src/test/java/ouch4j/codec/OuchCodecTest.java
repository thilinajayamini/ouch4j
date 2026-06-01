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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ouch4j.message.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OUCH 5.0 Encoder and Decoder.
 * Verifies binary serialization round-trips and exact byte-level correctness.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
class OuchCodecTest {

    private OuchEncoder encoder;
    private OuchDecoder decoder;

    @BeforeEach
    void setUp() {
        encoder = new OuchEncoder();
        decoder = new OuchDecoder();
    }

    // ==================== Enter Order Tests ====================

    @Test
    @DisplayName("Enter Order: encode produces correct binary layout")
    void testEncodeEnterOrder_binaryLayout() {
        EnterOrderMessage msg = createSampleEnterOrder();

        ByteBuffer buf = encoder.encodeEnterOrder(msg);

        assertEquals(EnterOrderMessage.FIXED_SIZE, buf.remaining());
        assertEquals((byte) 'O', buf.get(0), "Message type should be 'O'");
    }

    @Test
    @DisplayName("Enter Order: encode/decode round-trip preserves all fields")
    void testEnterOrder_roundTrip() {
        EnterOrderMessage original = createSampleEnterOrder();

        ByteBuffer encoded = encoder.encodeEnterOrder(original);
        EnterOrderMessage decoded = decoder.decodeEnterOrder(encoded);

        assertEquals(original.getOrderToken(), decoded.getOrderToken());
        assertEquals(original.getBuySellIndicator(), decoded.getBuySellIndicator());
        assertEquals(original.getShares(), decoded.getShares());
        assertEquals(original.getStock(), decoded.getStock());
        assertEquals(original.getPrice(), decoded.getPrice());
        assertEquals(original.getTimeInForce(), decoded.getTimeInForce());
        assertEquals(original.getDisplay(), decoded.getDisplay());
        assertEquals(original.getCapacity(), decoded.getCapacity());
        assertEquals(original.getIntermarketSweep(), decoded.getIntermarketSweep());
        assertEquals(original.getMinimumQuantity(), decoded.getMinimumQuantity());
        assertEquals(original.getCrossType(), decoded.getCrossType());
        assertEquals(original.getCustomerType(), decoded.getCustomerType());
    }

    @Test
    @DisplayName("Enter Order: alpha fields are correctly padded")
    void testEnterOrder_alphaPadding() {
        EnterOrderMessage msg = new EnterOrderMessage();
        msg.setOrderToken("ORD1");
        msg.setBuySellIndicator((byte) 'B');
        msg.setShares(100);
        msg.setStock("AAPL");
        msg.setPrice(1505000);
        msg.setTimeInForce(0);
        msg.setDisplay((byte) 'Y');
        msg.setCapacity((byte) 'A');
        msg.setIntermarketSweep((byte) 'N');
        msg.setMinimumQuantity(0);
        msg.setCrossType((byte) 'N');
        msg.setCustomerType((byte) ' ');

        ByteBuffer buf = encoder.encodeEnterOrder(msg);
        EnterOrderMessage decoded = decoder.decodeEnterOrder(buf);

        // Token should be padded to 14 chars
        assertEquals("ORD1          ", decoded.getOrderToken());
        // Stock should be padded to 8 chars
        assertEquals("AAPL    ", decoded.getStock());
    }

    @Test
    @DisplayName("Enter Order: big-endian integer encoding")
    void testEnterOrder_bigEndianIntegers() {
        EnterOrderMessage msg = createSampleEnterOrder();
        msg.setShares(256); // 0x00000100
        msg.setPrice(1505000); // 0x0016F7E8

        ByteBuffer buf = encoder.encodeEnterOrder(msg);

        // Shares at offset 16: should be 0x00 0x00 0x01 0x00
        assertEquals(0x00, buf.get(16) & 0xFF);
        assertEquals(0x00, buf.get(17) & 0xFF);
        assertEquals(0x01, buf.get(18) & 0xFF);
        assertEquals(0x00, buf.get(19) & 0xFF);
    }

    // ==================== Replace Order Tests ====================

    @Test
    @DisplayName("Replace Order: encode/decode round-trip")
    void testReplaceOrder_roundTrip() {
        ReplaceOrderMessage original = new ReplaceOrderMessage();
        original.setExistingOrderToken("ORD001");
        original.setReplacementOrderToken("ORD002");
        original.setShares(200);
        original.setPrice(1510000);
        original.setTimeInForce(0);
        original.setDisplay((byte) 'Y');
        original.setIntermarketSweep((byte) 'N');
        original.setMinimumQuantity(0);

        ByteBuffer encoded = encoder.encodeReplaceOrder(original);
        assertEquals(ReplaceOrderMessage.FIXED_SIZE, encoded.remaining());

        ReplaceOrderMessage decoded = decoder.decodeReplaceOrder(encoded);

        assertEquals("ORD001        ", decoded.getExistingOrderToken());
        assertEquals("ORD002        ", decoded.getReplacementOrderToken());
        assertEquals(200, decoded.getShares());
        assertEquals(1510000, decoded.getPrice());
    }

    // ==================== Cancel Order Tests ====================

    @Test
    @DisplayName("Cancel Order: encode/decode round-trip")
    void testCancelOrder_roundTrip() {
        CancelOrderMessage original = new CancelOrderMessage();
        original.setOrderToken("ORD001");
        original.setShares(0); // Cancel all

        ByteBuffer encoded = encoder.encodeCancelOrder(original);
        assertEquals(CancelOrderMessage.FIXED_SIZE, encoded.remaining());

        CancelOrderMessage decoded = decoder.decodeCancelOrder(encoded);

        assertEquals("ORD001        ", decoded.getOrderToken());
        assertEquals(0, decoded.getShares());
    }

    // ==================== Outbound Message Decode Tests ====================

    @Test
    @DisplayName("System Event: decode from binary")
    void testDecodeSystemEvent() {
        ByteBuffer buf = ByteBuffer.allocate(SystemEventMessage.SIZE);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put((byte) 'S');      // message type
        buf.putLong(123456789L);  // timestamp
        buf.put((byte) 'S');      // event code: Start of Day
        buf.flip();

        SystemEventMessage msg = (SystemEventMessage) decoder.decode(buf);

        assertEquals(MessageType.SYSTEM_EVENT, msg.getMessageType());
        assertEquals(123456789L, msg.getTimestamp());
        assertEquals((byte) 'S', msg.getEventCode());
    }

    @Test
    @DisplayName("Order Executed: decode from binary")
    void testDecodeOrderExecuted() {
        ByteBuffer buf = ByteBuffer.allocate(OrderExecutedMessage.FIXED_SIZE);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put((byte) 'E');            // type
        buf.putLong(999999L);           // timestamp
        writeAlpha(buf, "ORD001", 14);  // order token
        buf.putInt(50);                 // executed shares
        buf.putInt(1505000);            // execution price
        buf.put((byte) 'A');            // liquidity: Added
        buf.putLong(12345L);            // match number
        buf.flip();

        OrderExecutedMessage msg = (OrderExecutedMessage) decoder.decode(buf);

        assertEquals(MessageType.ORDER_EXECUTED, msg.getMessageType());
        assertEquals(50, msg.getExecutedShares());
        assertEquals(1505000, msg.getExecutionPrice());
        assertEquals((byte) 'A', msg.getLiquidityFlag());
        assertEquals(12345L, msg.getMatchNumber());
    }

    @Test
    @DisplayName("Order Canceled: decode from binary")
    void testDecodeOrderCanceled() {
        ByteBuffer buf = ByteBuffer.allocate(OrderCanceledMessage.FIXED_SIZE);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put((byte) 'C');            // type
        buf.putLong(999999L);           // timestamp
        writeAlpha(buf, "ORD001", 14);  // order token
        buf.putInt(100);                // decrement shares
        buf.put((byte) 'U');            // reason: User requested
        buf.flip();

        OrderCanceledMessage msg = (OrderCanceledMessage) decoder.decode(buf);

        assertEquals(MessageType.ORDER_CANCELED, msg.getMessageType());
        assertEquals(100, msg.getDecrementShares());
        assertEquals((byte) 'U', msg.getReason());
    }

    @Test
    @DisplayName("Order Rejected: decode from binary")
    void testDecodeOrderRejected() {
        ByteBuffer buf = ByteBuffer.allocate(OrderRejectedMessage.FIXED_SIZE);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put((byte) 'J');            // type
        buf.putLong(999999L);           // timestamp
        writeAlpha(buf, "ORD001", 14);  // order token
        buf.putShort((short) 42);       // reject reason code
        buf.flip();

        OrderRejectedMessage msg = (OrderRejectedMessage) decoder.decode(buf);

        assertEquals(MessageType.ORDER_REJECTED, msg.getMessageType());
        assertEquals(42, msg.getRejectReasonCode());
    }

    // ==================== Appendage Tests ====================

    @Test
    @DisplayName("Appendages: encode and decode TagValue appendages")
    void testAppendages_roundTrip() {
        EnterOrderMessage original = createSampleEnterOrder();
        original.addAppendage(1, new byte[]{0x01, 0x02, 0x03});
        original.addAppendage(100, new byte[]{(byte) 'Y'});

        ByteBuffer encoded = encoder.encodeEnterOrder(original);
        // Fixed size + appendage 1 (2+2+3) + appendage 100 (2+2+1) = 45+7+5 = 57
        assertEquals(EnterOrderMessage.FIXED_SIZE + 7 + 5, encoded.remaining());

        EnterOrderMessage decoded = decoder.decodeEnterOrder(encoded);

        assertEquals(2, decoded.getAppendages().size());
        assertArrayEquals(new byte[]{0x01, 0x02, 0x03}, decoded.getAppendages().get(1));
        assertArrayEquals(new byte[]{(byte) 'Y'}, decoded.getAppendages().get(100));
    }

    // ==================== Generic Decode Tests ====================

    @Test
    @DisplayName("Generic decode: dispatches to correct message type")
    void testGenericDecode_dispatch() {
        // Encode an Enter Order
        EnterOrderMessage original = createSampleEnterOrder();
        ByteBuffer encoded = encoder.encode(original);

        // Decode using generic decode
        OuchMessage decoded = decoder.decode(encoded);

        assertTrue(decoded instanceof EnterOrderMessage);
        assertEquals(MessageType.ENTER_ORDER, decoded.getMessageType());
    }

    @Test
    @DisplayName("Generic decode: throws on unknown message type")
    void testGenericDecode_unknownType() {
        ByteBuffer buf = ByteBuffer.allocate(1);
        buf.put((byte) 'Z'); // Unknown type
        buf.flip();

        assertThrows(IllegalArgumentException.class, () -> decoder.decode(buf));
    }

    // ==================== Price Conversion Tests ====================

    @Test
    @DisplayName("Price conversion: decimal to OUCH and back")
    void testPriceConversion() {
        assertEquals(1505000, ByteBufferUtil.priceToOuch(150.50));
        assertEquals(150.50, ByteBufferUtil.priceFromOuch(1505000), 0.001);

        assertEquals(1000000, ByteBufferUtil.priceToOuch(100.00));
        assertEquals(1, ByteBufferUtil.priceToOuch(0.0001));
    }

    // ==================== Helper Methods ====================

    private EnterOrderMessage createSampleEnterOrder() {
        EnterOrderMessage msg = new EnterOrderMessage();
        msg.setOrderToken("ORD001        ");
        msg.setBuySellIndicator((byte) 'B');
        msg.setShares(100);
        msg.setStock("AAPL    ");
        msg.setPrice(1505000);
        msg.setTimeInForce(0);
        msg.setDisplay((byte) 'Y');
        msg.setCapacity((byte) 'A');
        msg.setIntermarketSweep((byte) 'N');
        msg.setMinimumQuantity(0);
        msg.setCrossType((byte) 'N');
        msg.setCustomerType((byte) ' ');
        return msg;
    }

    private void writeAlpha(ByteBuffer buf, String value, int len) {
        ByteBufferUtil.writeAlpha(buf, value, len);
    }
}
