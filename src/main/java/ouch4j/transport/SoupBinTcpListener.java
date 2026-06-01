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

package ouch4j.transport;

/**
 * Listener interface for SoupBinTCP session events.
 *
 * Implement this to handle incoming OUCH messages and session lifecycle events.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public interface SoupBinTcpListener {

    /**
     * Called when a sequenced data packet is received from the server.
     * The payload contains an OUCH outbound message.
     *
     * @param sequenceNumber the server-assigned sequence number
     * @param payload the raw OUCH message bytes (decode with OuchDecoder)
     */
    void onSequencedData(long sequenceNumber, byte[] payload);

    /**
     * Called when the login is accepted by the server.
     *
     * @param session the session identifier
     * @param sequenceNumber the next expected sequence number
     */
    void onLoginAccepted(String session, long sequenceNumber);

    /**
     * Called when the login is rejected by the server.
     *
     * @param rejectReason the reason code ('A'=Not Authorized, 'S'=Session Not Available)
     */
    void onLoginRejected(char rejectReason);

    /**
     * Called when the server sends an End of Session packet.
     */
    void onEndOfSession();

    /**
     * Called when the server sends a debug message.
     *
     * @param text the debug text
     */
    void onDebug(String text);

    /**
     * Called when the connection is lost or an error occurs.
     *
     * @param cause the exception that caused the disconnect (may be null)
     */
    void onDisconnect(Throwable cause);
}
