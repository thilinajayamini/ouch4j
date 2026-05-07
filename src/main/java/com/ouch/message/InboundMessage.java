package com.ouch.message;

/**
 * Marker interface for inbound OUCH messages (Client → Exchange).
 *
 * Inbound messages are unsequenced and designed for safe retransmission.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public interface InboundMessage extends OuchMessage {
    // Marker interface only
}
