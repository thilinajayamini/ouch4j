package com.ouch.message;

import java.util.Map;

/**
 * Base interface for all OUCH 5.0 messages.
 *
 * Every OUCH message has a single-byte type identifier as the first field
 * and may contain optional TagValue appendages.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public interface OuchMessage {

    /**
     * Returns the OUCH message type byte (e.g., 'O' for Enter Order).
     */
    byte getMessageType();

    /**
     * Returns a human-readable name for this message type.
     */
    String getMessageTypeName();

    /**
     * Returns the optional TagValue appendages attached to this message.
     * Key = tag ID (unsigned short), Value = raw byte array.
     *
     * @return appendage map, never null (may be empty)
     */
    Map<Integer, byte[]> getAppendages();

    /**
     * Adds a TagValue appendage to this message.
     *
     * @param tagId the tag identifier
     * @param value the raw tag value bytes
     */
    void addAppendage(int tagId, byte[] value);
}
