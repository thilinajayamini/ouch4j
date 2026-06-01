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
 * SoupBinTCP packet type constants.
 *
 * SoupBinTCP is the session-layer transport protocol that carries
 * OUCH messages. It handles login, heartbeat, sequencing, and recovery.
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public final class SoupBinTcpPacketType {

    private SoupBinTcpPacketType() {}

    // ==================== Client → Server ====================

    /** Login Request - client authenticates with the server */
    public static final byte LOGIN_REQUEST = (byte) 'L';

    /** Unsequenced Data - client sends an OUCH inbound message */
    public static final byte UNSEQUENCED_DATA = (byte) 'U';

    /** Client Heartbeat - sent if no data for > 1 second */
    public static final byte CLIENT_HEARTBEAT = (byte) 'R';

    /** Logout Request - client requests session termination */
    public static final byte LOGOUT_REQUEST = (byte) 'O';

    // ==================== Server → Client ====================

    /** Login Accepted - server confirms authentication */
    public static final byte LOGIN_ACCEPTED = (byte) 'A';

    /** Login Rejected - server denies authentication */
    public static final byte LOGIN_REJECTED = (byte) 'J';

    /** Sequenced Data - server sends an OUCH outbound message */
    public static final byte SEQUENCED_DATA = (byte) 'S';

    /** Server Heartbeat - sent if no data for > 1 second */
    public static final byte SERVER_HEARTBEAT = (byte) 'H';

    /** End of Session - server terminates the session */
    public static final byte END_OF_SESSION = (byte) 'Z';

    /** Debug Packet - informational text from the server */
    public static final byte DEBUG = (byte) '+';
}
