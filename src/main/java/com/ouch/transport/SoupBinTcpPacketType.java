package com.ouch.transport;

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
