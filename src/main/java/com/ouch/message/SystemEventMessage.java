package com.ouch.message;

import com.ouch.constant.MessageType;

/**
 * OUCH 5.0 System Event Message (Type = 'S').
 *
 * Outbound message sent by the exchange to signal system-wide events.
 *
 * Binary layout:
 * | Offset | Length | Field                      |
 * |--------|--------|----------------------------|
 * | 0      | 1      | Message Type ('S')         |
 * | 1      | 8      | Timestamp                  |
 * | 9      | 1      | Event Code                 |
 *
 * Total size: 10 bytes
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class SystemEventMessage extends AbstractOuchMessage implements OutboundMessage {

    public static final int SIZE = 10;

    private long timestamp;      // 8 bytes - nanoseconds since midnight
    private byte eventCode;      // 1 byte - 'S' (Start of Day), 'E' (End of Day)

    @Override
    public byte getMessageType() {
        return MessageType.SYSTEM_EVENT;
    }

    // ========== Getters and Setters ==========

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte getEventCode() {
        return eventCode;
    }

    public void setEventCode(byte eventCode) {
        this.eventCode = eventCode;
    }

    @Override
    protected String toStringFields() {
        return "timestamp=" + timestamp +
                ", eventCode=" + (char) eventCode;
    }
}
