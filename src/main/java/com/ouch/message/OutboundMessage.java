package com.ouch.message;

/**
 * Marker interface for outbound OUCH messages (Exchange → Client).
 *
 * Outbound messages are sequenced via SoupBinTCP.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public interface OutboundMessage extends OuchMessage {
    // Marker interface only
}
