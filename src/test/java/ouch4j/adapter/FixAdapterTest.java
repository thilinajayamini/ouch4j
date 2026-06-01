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

package ouch4j.adapter;

import ouch4j.codec.ByteBufferUtil;
import ouch4j.message.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ouch4j.message.*;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.OrderCancelReplaceRequest;
import quickfix.fix44.OrderCancelRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the FIX↔OUCH adapter layer.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
class FixAdapterTest {

    private FixToOuchAdapter fixToOuch;
    private OuchToFixAdapter ouchToFix;

    @BeforeEach
    void setUp() {
        fixToOuch = new FixToOuchAdapter();
        ouchToFix = new OuchToFixAdapter();
    }

    // ==================== FIX → OUCH Tests ====================

    @Test
    @DisplayName("FIX NewOrderSingle → OUCH EnterOrder")
    void testNewOrderSingle_toEnterOrder() throws Exception {
        NewOrderSingle fix = new NewOrderSingle();
        fix.set(new ClOrdID("ORD001"));
        fix.set(new Side(Side.BUY));
        fix.set(new OrderQty(100));
        fix.set(new Symbol("AAPL"));
        fix.set(new Price(150.50));
        fix.set(new TimeInForce(TimeInForce.DAY));
        fix.set(new OrdType(OrdType.LIMIT));

        InboundMessage result = fixToOuch.convert(fix);

        assertTrue(result instanceof EnterOrderMessage);
        EnterOrderMessage ouch = (EnterOrderMessage) result;

        assertEquals("ORD001", ouch.getOrderToken());
        assertEquals((byte) 'B', ouch.getBuySellIndicator());
        assertEquals(100, ouch.getShares());
        assertEquals("AAPL", ouch.getStock());
        assertEquals(ByteBufferUtil.priceToOuch(150.50), ouch.getPrice());
        assertEquals(0, ouch.getTimeInForce()); // Day
    }

    @Test
    @DisplayName("FIX Side mapping: Sell → 'S'")
    void testSideMapping_sell() throws Exception {
        NewOrderSingle fix = new NewOrderSingle();
        fix.set(new ClOrdID("ORD002"));
        fix.set(new Side(Side.SELL));
        fix.set(new OrderQty(50));
        fix.set(new Symbol("MSFT"));
        fix.set(new Price(300.00));
        fix.set(new OrdType(OrdType.LIMIT));

        EnterOrderMessage ouch = (EnterOrderMessage) fixToOuch.convert(fix);
        assertEquals((byte) 'S', ouch.getBuySellIndicator());
    }

    @Test
    @DisplayName("FIX Side mapping: SellShort → 'T'")
    void testSideMapping_sellShort() throws Exception {
        NewOrderSingle fix = new NewOrderSingle();
        fix.set(new ClOrdID("ORD003"));
        fix.set(new Side(Side.SELL_SHORT));
        fix.set(new OrderQty(50));
        fix.set(new Symbol("TSLA"));
        fix.set(new Price(200.00));
        fix.set(new OrdType(OrdType.LIMIT));

        EnterOrderMessage ouch = (EnterOrderMessage) fixToOuch.convert(fix);
        assertEquals((byte) 'T', ouch.getBuySellIndicator());
    }

    @Test
    @DisplayName("FIX TimeInForce IOC → OUCH 99999")
    void testTimeInForce_ioc() throws Exception {
        NewOrderSingle fix = new NewOrderSingle();
        fix.set(new ClOrdID("ORD004"));
        fix.set(new Side(Side.BUY));
        fix.set(new OrderQty(100));
        fix.set(new Symbol("GOOG"));
        fix.set(new Price(100.00));
        fix.set(new TimeInForce(TimeInForce.IMMEDIATE_OR_CANCEL));
        fix.set(new OrdType(OrdType.LIMIT));

        EnterOrderMessage ouch = (EnterOrderMessage) fixToOuch.convert(fix);
        assertEquals(99999, ouch.getTimeInForce());
    }

    @Test
    @DisplayName("FIX OrderCancelReplaceRequest → OUCH ReplaceOrder")
    void testCancelReplace_toReplaceOrder() throws Exception {
        OrderCancelReplaceRequest fix = new OrderCancelReplaceRequest();
        fix.set(new OrigClOrdID("ORD001"));
        fix.set(new ClOrdID("ORD002"));
        fix.set(new Side(Side.BUY));
        fix.set(new OrderQty(200));
        fix.set(new Price(151.00));
        fix.set(new OrdType(OrdType.LIMIT));

        InboundMessage result = fixToOuch.convert(fix);

        assertTrue(result instanceof ReplaceOrderMessage);
        ReplaceOrderMessage ouch = (ReplaceOrderMessage) result;

        assertEquals("ORD001", ouch.getExistingOrderToken());
        assertEquals("ORD002", ouch.getReplacementOrderToken());
        assertEquals(200, ouch.getShares());
        assertEquals(ByteBufferUtil.priceToOuch(151.00), ouch.getPrice());
    }

    @Test
    @DisplayName("FIX OrderCancelRequest → OUCH CancelOrder")
    void testCancelRequest_toCancelOrder() throws Exception {
        OrderCancelRequest fix = new OrderCancelRequest();
        fix.set(new OrigClOrdID("ORD001"));
        fix.set(new ClOrdID("CXL001"));
        fix.set(new Side(Side.BUY));
        fix.set(new Symbol("AAPL"));

        InboundMessage result = fixToOuch.convert(fix);

        assertTrue(result instanceof CancelOrderMessage);
        CancelOrderMessage ouch = (CancelOrderMessage) result;

        assertEquals("ORD001", ouch.getOrderToken());
        assertEquals(0, ouch.getShares()); // Cancel all
    }

    // ==================== OUCH → FIX Tests ====================

    @Test
    @DisplayName("OUCH OrderAccepted → FIX ExecutionReport(New)")
    void testOrderAccepted_toExecutionReport() throws Exception {
        OrderAcceptedMessage ouch = new OrderAcceptedMessage();
        ouch.setTimestamp(123456789L);
        ouch.setOrderToken("ORD001        ");
        ouch.setBuySellIndicator((byte) 'B');
        ouch.setShares(100);
        ouch.setStock("AAPL    ");
        ouch.setPrice(1505000);
        ouch.setTimeInForce(0);
        ouch.setOrderReferenceNumber(99999L);
        ouch.setOrderState((byte) 'L');

        ExecutionReport fix = (ExecutionReport) ouchToFix.convert(ouch);

        assertEquals("ORD001", fix.getString(ClOrdID.FIELD));
        assertEquals(ExecType.NEW, fix.getChar(quickfix.field.ExecType.FIELD));
        assertEquals(OrdStatus.NEW, fix.getChar(quickfix.field.OrdStatus.FIELD));
        assertEquals(Side.BUY, fix.getChar(quickfix.field.Side.FIELD));
        assertEquals(100.0, fix.getDouble(quickfix.field.OrderQty.FIELD), 0.01);
        assertEquals("AAPL", fix.getString(quickfix.field.Symbol.FIELD));
        assertEquals(150.50, fix.getDouble(quickfix.field.Price.FIELD), 0.01);
    }

    @Test
    @DisplayName("OUCH OrderExecuted → FIX ExecutionReport(Trade)")
    void testOrderExecuted_toExecutionReport() throws Exception {
        OrderExecutedMessage ouch = new OrderExecutedMessage();
        ouch.setTimestamp(123456789L);
        ouch.setOrderToken("ORD001        ");
        ouch.setExecutedShares(50);
        ouch.setExecutionPrice(1505000);
        ouch.setLiquidityFlag((byte) 'A');
        ouch.setMatchNumber(12345L);

        ExecutionReport fix = (ExecutionReport) ouchToFix.convert(ouch);

        assertEquals("ORD001", fix.getString(ClOrdID.FIELD));
        assertEquals(ExecType.TRADE, fix.getChar(quickfix.field.ExecType.FIELD));
        assertEquals(50.0, fix.getDouble(LastQty.FIELD), 0.01);
        assertEquals(150.50, fix.getDouble(LastPx.FIELD), 0.01);
        assertEquals("12345", fix.getString(ExecID.FIELD));
    }

    @Test
    @DisplayName("OUCH OrderCanceled → FIX ExecutionReport(Canceled)")
    void testOrderCanceled_toExecutionReport() throws Exception {
        OrderCanceledMessage ouch = new OrderCanceledMessage();
        ouch.setTimestamp(123456789L);
        ouch.setOrderToken("ORD001        ");
        ouch.setDecrementShares(100);
        ouch.setReason((byte) 'U');

        ExecutionReport fix = (ExecutionReport) ouchToFix.convert(ouch);

        assertEquals("ORD001", fix.getString(ClOrdID.FIELD));
        assertEquals(ExecType.CANCELED, fix.getChar(quickfix.field.ExecType.FIELD));
        assertEquals(OrdStatus.CANCELED, fix.getChar(quickfix.field.OrdStatus.FIELD));
        assertTrue(fix.getString(Text.FIELD).contains("U"));
    }

    @Test
    @DisplayName("OUCH OrderRejected → FIX ExecutionReport(Rejected)")
    void testOrderRejected_toExecutionReport() throws Exception {
        OrderRejectedMessage ouch = new OrderRejectedMessage();
        ouch.setTimestamp(123456789L);
        ouch.setOrderToken("ORD001        ");
        ouch.setRejectReasonCode(42);

        ExecutionReport fix = (ExecutionReport) ouchToFix.convert(ouch);

        assertEquals("ORD001", fix.getString(ClOrdID.FIELD));
        assertEquals(ExecType.REJECTED, fix.getChar(quickfix.field.ExecType.FIELD));
        assertEquals(OrdStatus.REJECTED, fix.getChar(quickfix.field.OrdStatus.FIELD));
    }
}
