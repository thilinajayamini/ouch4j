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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Utility methods for reading/writing OUCH binary fields from/to ByteBuffer.
 *
 * OUCH data conventions:
 * - Integers: unsigned, big-endian (network byte order)
 * - Alpha: left-justified, right-padded with spaces (0x20)
 * - All ByteBuffers are assumed to use BIG_ENDIAN byte order (Java default)
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public final class ByteBufferUtil {

    private ByteBufferUtil() {
        // Utility class
    }

    // ==================== Alpha Field Operations ====================

    /**
     * Writes an alpha string field to the buffer.
     * Left-justified, right-padded with spaces to the specified length.
     *
     * @param buffer the target buffer
     * @param value  the string value (may be shorter than fieldLength)
     * @param fieldLength the fixed field length in bytes
     */
    public static void writeAlpha(ByteBuffer buffer, String value, int fieldLength) {
        byte[] bytes = new byte[fieldLength];
        Arrays.fill(bytes, (byte) ' '); // Pad with spaces

        if (value != null) {
            byte[] valueBytes = value.getBytes(StandardCharsets.US_ASCII);
            int copyLen = Math.min(valueBytes.length, fieldLength);
            System.arraycopy(valueBytes, 0, bytes, 0, copyLen);
        }
        buffer.put(bytes);
    }

    /**
     * Reads an alpha string field from the buffer.
     * Returns the string with trailing spaces preserved.
     *
     * @param buffer the source buffer
     * @param fieldLength the number of bytes to read
     * @return the alpha string (may contain trailing spaces)
     */
    public static String readAlpha(ByteBuffer buffer, int fieldLength) {
        byte[] bytes = new byte[fieldLength];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    /**
     * Reads an alpha string field and trims trailing spaces.
     */
    public static String readAlphaTrimmed(ByteBuffer buffer, int fieldLength) {
        return readAlpha(buffer, fieldLength).trim();
    }

    // ==================== Integer Field Operations ====================

    /**
     * Writes a 1-byte value to the buffer.
     */
    public static void writeByte(ByteBuffer buffer, byte value) {
        buffer.put(value);
    }

    /**
     * Reads a 1-byte value from the buffer.
     */
    public static byte readByte(ByteBuffer buffer) {
        return buffer.get();
    }

    /**
     * Writes a 2-byte unsigned short (big-endian) to the buffer.
     */
    public static void writeUnsignedShort(ByteBuffer buffer, int value) {
        buffer.putShort((short) (value & 0xFFFF));
    }

    /**
     * Reads a 2-byte unsigned short (big-endian) from the buffer.
     */
    public static int readUnsignedShort(ByteBuffer buffer) {
        return buffer.getShort() & 0xFFFF;
    }

    /**
     * Writes a 4-byte unsigned int (big-endian) to the buffer.
     * Note: Java int is 32-bit signed, but we treat values as unsigned on the wire.
     */
    public static void writeInt(ByteBuffer buffer, int value) {
        buffer.putInt(value);
    }

    /**
     * Reads a 4-byte int (big-endian) from the buffer.
     */
    public static int readInt(ByteBuffer buffer) {
        return buffer.getInt();
    }

    /**
     * Writes an 8-byte long (big-endian) to the buffer.
     */
    public static void writeLong(ByteBuffer buffer, long value) {
        buffer.putLong(value);
    }

    /**
     * Reads an 8-byte long (big-endian) from the buffer.
     */
    public static long readLong(ByteBuffer buffer) {
        return buffer.getLong();
    }

    // ==================== Price Conversion ====================

    /** Default OUCH price scaling factor (4 decimal places) */
    public static final int PRICE_SCALE = 10000;

    /**
     * Converts a decimal price (e.g., 150.50) to an OUCH scaled integer.
     *
     * @param price the decimal price
     * @return the scaled integer price
     */
    public static int priceToOuch(double price) {
        return (int) Math.round(price * PRICE_SCALE);
    }

    /**
     * Converts an OUCH scaled integer price to a decimal price.
     *
     * @param ouchPrice the scaled integer price
     * @return the decimal price
     */
    public static double priceFromOuch(int ouchPrice) {
        return (double) ouchPrice / PRICE_SCALE;
    }

    /**
     * Converts a decimal price to OUCH scaled integer with a custom scale.
     */
    public static int priceToOuch(double price, int scale) {
        return (int) Math.round(price * scale);
    }

    /**
     * Converts an OUCH scaled integer price to decimal with a custom scale.
     */
    public static double priceFromOuch(int ouchPrice, int scale) {
        return (double) ouchPrice / scale;
    }
}
