package com.ouch.adapter;

import com.ouch.codec.ByteBufferUtil;
import com.ouch.constant.Side;
import com.ouch.constant.TimeInForce;
import com.ouch.message.CancelOrderMessage;
import com.ouch.message.EnterOrderMessage;
import com.ouch.message.ReplaceOrderMessage;
import com.ouch.message.InboundMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.*;

/**
 * Adapter to convert QuickFIX/J FIX messages into OUCH 5.0 inbound messages.
 *
 * Supports:
 * - FIX NewOrderSingle (MsgType=D) → OUCH Enter Order ('O')
 * - FIX OrderCancelReplaceRequest (MsgType=G) → OUCH Replace Order ('U')
 * - FIX OrderCancelRequest (MsgType=F) → OUCH Cancel Order ('X')
 *
 * Usage:
 * <pre>
 *   FixToOuchAdapter adapter = new FixToOuchAdapter();
 *   Message fixMsg = ...; // QuickFIX/J message
 *   InboundMessage ouch = adapter.convert(fixMsg);
 * </pre>
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class FixToOuchAdapter {

    private static final Logger log = LoggerFactory.getLogger(FixToOuchAdapter.class);

    /** Price scaling factor. Override via constructor for exchange-specific scaling. */
    private final int priceScale;

    /**
     * Creates a FixToOuchAdapter with the default price scale (10000).
     */
    public FixToOuchAdapter() {
        this(ByteBufferUtil.PRICE_SCALE);
    }

    /**
     * Creates a FixToOuchAdapter with a custom price scale.
     *
     * @param priceScale the price multiplier (e.g., 10000 for 4 decimal places)
     */
    public FixToOuchAdapter(int priceScale) {
        this.priceScale = priceScale;
    }

    /**
     * Converts a QuickFIX/J FIX message into the appropriate OUCH inbound message.
     *
     * @param fixMessage the FIX message to convert
     * @return the corresponding OUCH inbound message
     * @throws FieldNotFound if a required FIX field is missing
     * @throws IllegalArgumentException if the FIX MsgType is not supported
     */
    public InboundMessage convert(Message fixMessage) throws FieldNotFound {
        String msgType = fixMessage.getHeader().getString(MsgType.FIELD);

        switch (msgType) {
            case MsgType.ORDER_SINGLE:
                return convertNewOrderSingle(fixMessage);
            case MsgType.ORDER_CANCEL_REPLACE_REQUEST:
                return convertOrderCancelReplace(fixMessage);
            case MsgType.ORDER_CANCEL_REQUEST:
                return convertOrderCancelRequest(fixMessage);
            default:
                throw new IllegalArgumentException(
                        "Unsupported FIX MsgType for OUCH conversion: " + msgType);
        }
    }

    /**
     * Converts FIX NewOrderSingle (MsgType=D) → OUCH Enter Order ('O').
     */
    public EnterOrderMessage convertNewOrderSingle(Message fixMsg) throws FieldNotFound {
        EnterOrderMessage ouch = new EnterOrderMessage();

        // ClOrdID (tag 11) → Order Token
        ouch.setOrderToken(fixMsg.getString(ClOrdID.FIELD));

        // Side (tag 54) → Buy/Sell Indicator
        char fixSide = fixMsg.getChar(quickfix.field.Side.FIELD);
        ouch.setBuySellIndicator(Side.fromFixSide(fixSide).getValue());

        // OrderQty (tag 38) → Shares
        ouch.setShares((int) fixMsg.getDouble(OrderQty.FIELD));

        // Symbol (tag 55) → Stock
        ouch.setStock(fixMsg.getString(Symbol.FIELD));

        // Price (tag 44) → Price (scaled integer)
        if (fixMsg.isSetField(Price.FIELD)) {
            double fixPrice = fixMsg.getDouble(Price.FIELD);
            ouch.setPrice(ByteBufferUtil.priceToOuch(fixPrice, priceScale));
        }

        // TimeInForce (tag 59) → Time in Force
        if (fixMsg.isSetField(quickfix.field.TimeInForce.FIELD)) {
            char fixTif = fixMsg.getChar(quickfix.field.TimeInForce.FIELD);
            ouch.setTimeInForce(TimeInForce.fromFixTimeInForce(fixTif));
        } else {
            ouch.setTimeInForce(TimeInForce.MARKET_HOURS.getValue()); // Default: Day
        }

        // Display - default to Anonymous/Displayed
        ouch.setDisplay((byte) 'Y');

        // Capacity - default to Agency
        ouch.setCapacity((byte) 'A');

        // Intermarket Sweep - default to Not Eligible
        ouch.setIntermarketSweep((byte) 'N');

        // Minimum Quantity (tag 110)
        if (fixMsg.isSetField(MinQty.FIELD)) {
            ouch.setMinimumQuantity((int) fixMsg.getDouble(MinQty.FIELD));
        }

        // Cross Type - default to No Cross
        ouch.setCrossType((byte) 'N');

        // Customer Type - default to Retail
        ouch.setCustomerType((byte) ' ');

        log.debug("Converted FIX NewOrderSingle → OUCH EnterOrder: {}", ouch);
        return ouch;
    }

    /**
     * Converts FIX OrderCancelReplaceRequest (MsgType=G) → OUCH Replace Order ('U').
     */
    public ReplaceOrderMessage convertOrderCancelReplace(Message fixMsg) throws FieldNotFound {
        ReplaceOrderMessage ouch = new ReplaceOrderMessage();

        // OrigClOrdID (tag 41) → Existing Order Token
        ouch.setExistingOrderToken(fixMsg.getString(OrigClOrdID.FIELD));

        // ClOrdID (tag 11) → Replacement Order Token
        ouch.setReplacementOrderToken(fixMsg.getString(ClOrdID.FIELD));

        // OrderQty (tag 38) → Shares
        ouch.setShares((int) fixMsg.getDouble(OrderQty.FIELD));

        // Price (tag 44) → Price (scaled integer)
        if (fixMsg.isSetField(Price.FIELD)) {
            double fixPrice = fixMsg.getDouble(Price.FIELD);
            ouch.setPrice(ByteBufferUtil.priceToOuch(fixPrice, priceScale));
        }

        // TimeInForce (tag 59) → Time in Force
        if (fixMsg.isSetField(quickfix.field.TimeInForce.FIELD)) {
            char fixTif = fixMsg.getChar(quickfix.field.TimeInForce.FIELD);
            ouch.setTimeInForce(TimeInForce.fromFixTimeInForce(fixTif));
        } else {
            ouch.setTimeInForce(TimeInForce.MARKET_HOURS.getValue());
        }

        // Display - default to Anonymous/Displayed
        ouch.setDisplay((byte) 'Y');

        // Intermarket Sweep - default to Not Eligible
        ouch.setIntermarketSweep((byte) 'N');

        // Minimum Quantity (tag 110)
        if (fixMsg.isSetField(MinQty.FIELD)) {
            ouch.setMinimumQuantity((int) fixMsg.getDouble(MinQty.FIELD));
        }

        log.debug("Converted FIX OrderCancelReplace → OUCH ReplaceOrder: {}", ouch);
        return ouch;
    }

    /**
     * Converts FIX OrderCancelRequest (MsgType=F) → OUCH Cancel Order ('X').
     */
    public CancelOrderMessage convertOrderCancelRequest(Message fixMsg) throws FieldNotFound {
        CancelOrderMessage ouch = new CancelOrderMessage();

        // OrigClOrdID (tag 41) → Order Token
        ouch.setOrderToken(fixMsg.getString(OrigClOrdID.FIELD));

        // OrderQty (tag 38) → Shares (0 = cancel all)
        if (fixMsg.isSetField(OrderQty.FIELD)) {
            ouch.setShares((int) fixMsg.getDouble(OrderQty.FIELD));
        } else {
            ouch.setShares(0); // Cancel entire order
        }

        log.debug("Converted FIX OrderCancelRequest → OUCH CancelOrder: {}", ouch);
        return ouch;
    }
}
