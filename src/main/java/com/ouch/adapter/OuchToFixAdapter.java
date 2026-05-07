package com.ouch.adapter;

import com.ouch.codec.ByteBufferUtil;
import com.ouch.constant.MessageType;
import com.ouch.constant.Side;
import com.ouch.message.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.Message;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;

/**
 * Adapter to convert OUCH 5.0 outbound messages into QuickFIX/J FIX messages.
 *
 * Supports:
 * - OUCH Order Accepted ('A') → FIX ExecutionReport (ExecType=New)
 * - OUCH Order Replaced ('R') → FIX ExecutionReport (ExecType=Replaced)
 * - OUCH Order Executed ('E') → FIX ExecutionReport (ExecType=Trade)
 * - OUCH Order Canceled ('C') → FIX ExecutionReport (ExecType=Canceled)
 * - OUCH Order Rejected ('J') → FIX ExecutionReport (ExecType=Rejected)
 *
 * Thread-safety: This class is stateless and thread-safe.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class OuchToFixAdapter {

    private static final Logger log = LoggerFactory.getLogger(OuchToFixAdapter.class);

    private final int priceScale;

    public OuchToFixAdapter() {
        this(ByteBufferUtil.PRICE_SCALE);
    }

    public OuchToFixAdapter(int priceScale) {
        this.priceScale = priceScale;
    }

    /**
     * Converts an OUCH outbound message into a QuickFIX/J FIX ExecutionReport.
     *
     * @param ouchMessage the OUCH outbound message
     * @return the FIX ExecutionReport message
     * @throws IllegalArgumentException if the message type is not supported
     */
    public Message convert(OutboundMessage ouchMessage) {
        byte type = ouchMessage.getMessageType();

        switch (type) {
            case MessageType.ORDER_ACCEPTED:
                return convertOrderAccepted((OrderAcceptedMessage) ouchMessage);
            case MessageType.ORDER_REPLACED:
                return convertOrderReplaced((OrderReplacedMessage) ouchMessage);
            case MessageType.ORDER_EXECUTED:
                return convertOrderExecuted((OrderExecutedMessage) ouchMessage);
            case MessageType.ORDER_CANCELED:
                return convertOrderCanceled((OrderCanceledMessage) ouchMessage);
            case MessageType.ORDER_REJECTED:
                return convertOrderRejected((OrderRejectedMessage) ouchMessage);
            default:
                throw new IllegalArgumentException(
                        "Unsupported OUCH message type for FIX conversion: "
                                + MessageType.name(type));
        }
    }

    /**
     * OUCH Order Accepted ('A') → FIX ExecutionReport (ExecType=0 New).
     */
    public ExecutionReport convertOrderAccepted(OrderAcceptedMessage ouch) {
        ExecutionReport fix = new ExecutionReport();

        fix.set(new ClOrdID(ouch.getOrderToken().trim()));
        fix.set(new OrderID(String.valueOf(ouch.getOrderReferenceNumber())));
        fix.set(new ExecID(String.valueOf(ouch.getOrderReferenceNumber())));
        fix.set(new ExecType(ExecType.NEW));
        fix.set(new OrdStatus(OrdStatus.NEW));
        fix.set(new quickfix.field.Side(
                Side.fromValue(ouch.getBuySellIndicator()).toFixSide()));
        fix.set(new OrderQty(ouch.getShares()));
        fix.set(new Symbol(ouch.getStock().trim()));
        fix.set(new Price(ByteBufferUtil.priceFromOuch(ouch.getPrice(), priceScale)));
        fix.set(new LeavesQty(ouch.getShares()));
        fix.set(new CumQty(0));
        fix.set(new AvgPx(0));

        log.debug("Converted OUCH OrderAccepted → FIX ExecutionReport(New): {}",
                ouch.getOrderToken().trim());
        return fix;
    }

    /**
     * OUCH Order Replaced ('R') → FIX ExecutionReport (ExecType=5 Replaced).
     */
    public ExecutionReport convertOrderReplaced(OrderReplacedMessage ouch) {
        ExecutionReport fix = new ExecutionReport();

        fix.set(new ClOrdID(ouch.getReplacementOrderToken().trim()));
        fix.set(new OrderID(String.valueOf(ouch.getOrderReferenceNumber())));
        fix.set(new ExecID(String.valueOf(ouch.getOrderReferenceNumber())));
        fix.set(new ExecType(ExecType.REPLACED));
        fix.set(new OrdStatus(OrdStatus.REPLACED));
        fix.set(new quickfix.field.Side(
                Side.fromValue(ouch.getBuySellIndicator()).toFixSide()));
        fix.set(new OrderQty(ouch.getShares()));
        fix.set(new Symbol(ouch.getStock().trim()));
        fix.set(new Price(ByteBufferUtil.priceFromOuch(ouch.getPrice(), priceScale)));
        fix.set(new LeavesQty(ouch.getShares()));
        fix.set(new CumQty(0));
        fix.set(new AvgPx(0));

        if (ouch.getPreviousOrderToken() != null) {
            fix.set(new OrigClOrdID(ouch.getPreviousOrderToken().trim()));
        }

        log.debug("Converted OUCH OrderReplaced → FIX ExecutionReport(Replaced): {}",
                ouch.getReplacementOrderToken().trim());
        return fix;
    }

    /**
     * OUCH Order Executed ('E') → FIX ExecutionReport (ExecType=F Trade).
     */
    public ExecutionReport convertOrderExecuted(OrderExecutedMessage ouch) {
        ExecutionReport fix = new ExecutionReport();

        fix.set(new ClOrdID(ouch.getOrderToken().trim()));
        fix.set(new OrderID(ouch.getOrderToken().trim()));
        fix.set(new ExecID(String.valueOf(ouch.getMatchNumber())));
        fix.set(new ExecType(ExecType.TRADE));
        fix.set(new OrdStatus(OrdStatus.PARTIALLY_FILLED)); // Caller can refine
        fix.set(new LastQty(ouch.getExecutedShares()));
        fix.set(new LastPx(ByteBufferUtil.priceFromOuch(ouch.getExecutionPrice(), priceScale)));
        fix.set(new LeavesQty(0)); // Caller must set from order state
        fix.set(new CumQty(ouch.getExecutedShares())); // Caller should accumulate
        fix.set(new AvgPx(ByteBufferUtil.priceFromOuch(ouch.getExecutionPrice(), priceScale)));

        log.debug("Converted OUCH OrderExecuted → FIX ExecutionReport(Trade): {} qty={}",
                ouch.getOrderToken().trim(), ouch.getExecutedShares());
        return fix;
    }

    /**
     * OUCH Order Canceled ('C') → FIX ExecutionReport (ExecType=4 Canceled).
     */
    public ExecutionReport convertOrderCanceled(OrderCanceledMessage ouch) {
        ExecutionReport fix = new ExecutionReport();

        fix.set(new ClOrdID(ouch.getOrderToken().trim()));
        fix.set(new OrderID(ouch.getOrderToken().trim()));
        fix.set(new ExecID("CXL-" + ouch.getOrderToken().trim()));
        fix.set(new ExecType(ExecType.CANCELED));
        fix.set(new OrdStatus(OrdStatus.CANCELED));
        fix.set(new LeavesQty(0));
        fix.set(new CumQty(0));
        fix.set(new AvgPx(0));

        // Map OUCH cancel reason to FIX text
        fix.set(new Text("Cancel reason: " + (char) ouch.getReason()));

        log.debug("Converted OUCH OrderCanceled → FIX ExecutionReport(Canceled): {} reason={}",
                ouch.getOrderToken().trim(), (char) ouch.getReason());
        return fix;
    }

    /**
     * OUCH Order Rejected ('J') → FIX ExecutionReport (ExecType=8 Rejected).
     */
    public ExecutionReport convertOrderRejected(OrderRejectedMessage ouch) {
        ExecutionReport fix = new ExecutionReport();

        fix.set(new ClOrdID(ouch.getOrderToken().trim()));
        fix.set(new OrderID(ouch.getOrderToken().trim()));
        fix.set(new ExecID("REJ-" + ouch.getOrderToken().trim()));
        fix.set(new ExecType(ExecType.REJECTED));
        fix.set(new OrdStatus(OrdStatus.REJECTED));
        fix.set(new OrdRejReason(ouch.getRejectReasonCode()));
        fix.set(new LeavesQty(0));
        fix.set(new CumQty(0));
        fix.set(new AvgPx(0));
        fix.set(new Text("OUCH reject code: " + ouch.getRejectReasonCode()));

        log.debug("Converted OUCH OrderRejected → FIX ExecutionReport(Rejected): {} code={}",
                ouch.getOrderToken().trim(), ouch.getRejectReasonCode());
        return fix;
    }
}
