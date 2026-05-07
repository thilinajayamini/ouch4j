package com.ouch.message;

import com.ouch.constant.MessageType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class providing common functionality for all OUCH messages.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public abstract class AbstractOuchMessage implements OuchMessage {

    private final Map<Integer, byte[]> appendages = new HashMap<>();

    @Override
    public String getMessageTypeName() {
        return MessageType.name(getMessageType());
    }

    @Override
    public Map<Integer, byte[]> getAppendages() {
        return Collections.unmodifiableMap(appendages);
    }

    @Override
    public void addAppendage(int tagId, byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("Appendage value cannot be null");
        }
        appendages.put(tagId, value);
    }

    /**
     * Calculates the total byte length of all appendages.
     * Each appendage contributes: 2 (tagId) + 2 (length) + value.length
     */
    public int getAppendagesLength() {
        int len = 0;
        for (Map.Entry<Integer, byte[]> entry : appendages.entrySet()) {
            len += 2 + 2 + entry.getValue().length; // tagId(2) + length(2) + value
        }
        return len;
    }

    @Override
    public String toString() {
        return getMessageTypeName() + "{" + toStringFields() + "}";
    }

    /**
     * Subclasses override this to provide field descriptions for toString().
     */
    protected abstract String toStringFields();
}
