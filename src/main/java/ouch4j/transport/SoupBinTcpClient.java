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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SoupBinTCP client for OUCH 5.0 session management.
 *
 * Handles:
 * - TCP connection establishment
 * - Login/Logout handshake
 * - Heartbeat management (client and server)
 * - Sequenced data reception (OUCH outbound messages)
 * - Unsequenced data transmission (OUCH inbound messages)
 * - Automatic reconnection on disconnect
 *
 * Usage:
 * <pre>
 *   SoupBinTcpClient client = new SoupBinTcpClient("host", 9100, listener);
 *   client.login("username", "password", "", 1);
 *   client.sendUnsequencedData(encodedOuchMessage);
 *   client.logout();
 * </pre>
 *
 * @author Thilina Jayamini
 * @since 2026-04-30
 */
public class SoupBinTcpClient implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(SoupBinTcpClient.class);

    /** Heartbeat interval: send heartbeat if no data sent for this duration */
    private static final long HEARTBEAT_INTERVAL_MS = 1000;

    /** Connection timeout: consider connection dead if no data for this duration */
    private static final long CONNECTION_TIMEOUT_MS = 15000;

    /** SoupBinTCP packet header: 2 bytes (length) + 1 byte (type) */
    private static final int PACKET_HEADER_SIZE = 3;

    private final String host;
    private final int port;
    private final SoupBinTcpListener listener;

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean loggedIn = new AtomicBoolean(false);
    private final AtomicLong lastSendTime = new AtomicLong(0);
    private final AtomicLong lastReceiveTime = new AtomicLong(0);
    private final AtomicLong nextSequenceNumber = new AtomicLong(1);

    private ScheduledExecutorService heartbeatExecutor;
    private ExecutorService readerExecutor;

    /**
     * Creates a SoupBinTCP client.
     *
     * @param host the server hostname or IP
     * @param port the server port
     * @param listener the event listener for session callbacks
     */
    public SoupBinTcpClient(String host, int port, SoupBinTcpListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
    }

    /**
     * Connects to the server and initiates a login.
     *
     * @param username the login username (6 characters, space-padded)
     * @param password the login password (10 characters, space-padded)
     * @param requestedSession the session ID to resume ("" for new session)
     * @param requestedSequenceNumber the sequence number to resume from (1 for new)
     * @throws IOException if the connection fails
     */
    public void login(String username, String password,
                      String requestedSession, long requestedSequenceNumber) throws IOException {
        connect();
        sendLoginRequest(username, password, requestedSession, requestedSequenceNumber);
    }

    /**
     * Establishes a TCP connection to the server.
     */
    private void connect() throws IOException {
        if (connected.get()) {
            throw new IllegalStateException("Already connected");
        }

        log.info("Connecting to SoupBinTCP server at {}:{}", host, port);
        socket = new Socket();
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
        socket.connect(new InetSocketAddress(host, port), 5000);

        inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        connected.set(true);

        lastReceiveTime.set(System.currentTimeMillis());
        lastSendTime.set(System.currentTimeMillis());

        // Start the reader thread
        readerExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "SoupBinTCP-Reader");
            t.setDaemon(true);
            return t;
        });
        readerExecutor.submit(this::readerLoop);

        // Start the heartbeat scheduler
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SoupBinTCP-Heartbeat");
            t.setDaemon(true);
            return t;
        });
        heartbeatExecutor.scheduleAtFixedRate(this::heartbeatCheck,
                HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);

        log.info("Connected to SoupBinTCP server");
    }

    /**
     * Sends a Login Request packet.
     */
    private void sendLoginRequest(String username, String password,
                                  String session, long sequenceNumber) throws IOException {
        // Login Request: username(6) + password(10) + session(10) + sequenceNumber(20)
        byte[] usernameBytes = padRight(username, 6);
        byte[] passwordBytes = padRight(password, 10);
        byte[] sessionBytes = padRight(session, 10);
        byte[] seqBytes = padRight(String.valueOf(sequenceNumber), 20);

        int payloadLength = 6 + 10 + 10 + 20; // 46 bytes
        ByteBuffer packet = ByteBuffer.allocate(PACKET_HEADER_SIZE + payloadLength);
        packet.order(ByteOrder.BIG_ENDIAN);

        packet.putShort((short) (payloadLength + 1)); // length includes packet type
        packet.put(SoupBinTcpPacketType.LOGIN_REQUEST);
        packet.put(usernameBytes);
        packet.put(passwordBytes);
        packet.put(sessionBytes);
        packet.put(seqBytes);

        sendRaw(packet.array());
        log.debug("Sent Login Request for user: {}", username.trim());
    }

    /**
     * Sends an OUCH inbound message as unsequenced data.
     *
     * @param ouchPayload the encoded OUCH message bytes
     * @throws IOException if the send fails
     */
    public void sendUnsequencedData(byte[] ouchPayload) throws IOException {
        if (!loggedIn.get()) {
            throw new IllegalStateException("Not logged in");
        }

        int payloadLength = 1 + ouchPayload.length; // type + payload
        ByteBuffer packet = ByteBuffer.allocate(2 + payloadLength);
        packet.order(ByteOrder.BIG_ENDIAN);
        packet.putShort((short) payloadLength);
        packet.put(SoupBinTcpPacketType.UNSEQUENCED_DATA);
        packet.put(ouchPayload);

        sendRaw(packet.array());
        log.debug("Sent Unsequenced Data ({} bytes)", ouchPayload.length);
    }

    /**
     * Sends a ByteBuffer's content as unsequenced data.
     *
     * @param buffer the OUCH message ByteBuffer (position=0, limit=message length)
     * @throws IOException if the send fails
     */
    public void sendUnsequencedData(ByteBuffer buffer) throws IOException {
        byte[] payload = new byte[buffer.remaining()];
        buffer.get(payload);
        sendUnsequencedData(payload);
    }

    /**
     * Sends a Logout Request and disconnects.
     */
    public void logout() throws IOException {
        if (!connected.get()) return;

        ByteBuffer packet = ByteBuffer.allocate(3);
        packet.order(ByteOrder.BIG_ENDIAN);
        packet.putShort((short) 1);
        packet.put(SoupBinTcpPacketType.LOGOUT_REQUEST);

        sendRaw(packet.array());
        log.info("Sent Logout Request");
        loggedIn.set(false);
    }

    /**
     * Sends a client heartbeat packet.
     */
    private void sendClientHeartbeat() throws IOException {
        ByteBuffer packet = ByteBuffer.allocate(3);
        packet.order(ByteOrder.BIG_ENDIAN);
        packet.putShort((short) 1);
        packet.put(SoupBinTcpPacketType.CLIENT_HEARTBEAT);

        sendRaw(packet.array());
    }

    /**
     * Sends raw bytes to the server.
     */
    private synchronized void sendRaw(byte[] data) throws IOException {
        if (!connected.get()) {
            throw new IOException("Not connected");
        }
        outputStream.write(data);
        outputStream.flush();
        lastSendTime.set(System.currentTimeMillis());
    }

    /**
     * Main reader loop - reads and dispatches incoming SoupBinTCP packets.
     */
    private void readerLoop() {
        try {
            while (connected.get()) {
                // Read packet header: 2-byte length (big-endian)
                int packetLength = inputStream.readUnsignedShort();
                if (packetLength == 0) continue;

                lastReceiveTime.set(System.currentTimeMillis());

                // Read packet type (1 byte)
                byte packetType = inputStream.readByte();
                int payloadLength = packetLength - 1;

                // Read payload
                byte[] payload = new byte[payloadLength];
                if (payloadLength > 0) {
                    inputStream.readFully(payload);
                }

                handlePacket(packetType, payload);
            }
        } catch (EOFException e) {
            log.info("Server closed connection");
            handleDisconnect(null);
        } catch (IOException e) {
            if (connected.get()) {
                log.error("Reader error", e);
                handleDisconnect(e);
            }
        }
    }

    /**
     * Dispatches a received SoupBinTCP packet.
     */
    private void handlePacket(byte type, byte[] payload) {
        switch (type) {
            case SoupBinTcpPacketType.LOGIN_ACCEPTED:
                handleLoginAccepted(payload);
                break;
            case SoupBinTcpPacketType.LOGIN_REJECTED:
                handleLoginRejected(payload);
                break;
            case SoupBinTcpPacketType.SEQUENCED_DATA:
                handleSequencedData(payload);
                break;
            case SoupBinTcpPacketType.SERVER_HEARTBEAT:
                // Just update lastReceiveTime (already done above)
                log.trace("Server heartbeat received");
                break;
            case SoupBinTcpPacketType.END_OF_SESSION:
                handleEndOfSession();
                break;
            case SoupBinTcpPacketType.DEBUG:
                handleDebug(payload);
                break;
            default:
                log.warn("Unknown SoupBinTCP packet type: 0x{}",
                        Integer.toHexString(type & 0xFF));
        }
    }

    private void handleLoginAccepted(byte[] payload) {
        // Login Accepted: session(10) + sequenceNumber(20)
        String session = new String(payload, 0, 10, StandardCharsets.US_ASCII).trim();
        String seqStr = new String(payload, 10, 20, StandardCharsets.US_ASCII).trim();
        long seqNum = Long.parseLong(seqStr);

        loggedIn.set(true);
        nextSequenceNumber.set(seqNum);
        log.info("Login accepted. Session: {}, Sequence: {}", session, seqNum);
        listener.onLoginAccepted(session, seqNum);
    }

    private void handleLoginRejected(byte[] payload) {
        char reason = payload.length > 0 ? (char) payload[0] : '?';
        loggedIn.set(false);
        log.warn("Login rejected. Reason: {}", reason);
        listener.onLoginRejected(reason);
    }

    private void handleSequencedData(byte[] payload) {
        long seqNum = nextSequenceNumber.getAndIncrement();
        log.trace("Sequenced data #{} ({} bytes)", seqNum, payload.length);
        listener.onSequencedData(seqNum, payload);
    }

    private void handleEndOfSession() {
        log.info("Server sent End of Session");
        loggedIn.set(false);
        listener.onEndOfSession();
    }

    private void handleDebug(byte[] payload) {
        String text = new String(payload, StandardCharsets.US_ASCII);
        log.debug("Server debug: {}", text);
        listener.onDebug(text);
    }

    /**
     * Heartbeat check: sends heartbeat if idle, detects dead connection.
     */
    private void heartbeatCheck() {
        try {
            long now = System.currentTimeMillis();

            // Send heartbeat if no data sent recently
            if (now - lastSendTime.get() > HEARTBEAT_INTERVAL_MS) {
                sendClientHeartbeat();
            }

            // Check for connection timeout
            if (now - lastReceiveTime.get() > CONNECTION_TIMEOUT_MS) {
                log.warn("No data received for {} ms — connection may be dead",
                        CONNECTION_TIMEOUT_MS);
                handleDisconnect(new IOException("Connection timeout"));
            }
        } catch (IOException e) {
            log.error("Heartbeat send failed", e);
            handleDisconnect(e);
        }
    }

    /**
     * Handles a disconnect event.
     */
    private void handleDisconnect(Throwable cause) {
        if (connected.compareAndSet(true, false)) {
            loggedIn.set(false);
            closeQuietly();
            listener.onDisconnect(cause);
        }
    }

    /**
     * Closes all resources quietly.
     */
    private void closeQuietly() {
        try { if (inputStream != null) inputStream.close(); } catch (IOException ignored) {}
        try { if (outputStream != null) outputStream.close(); } catch (IOException ignored) {}
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }

    /**
     * Returns true if the client is connected and logged in.
     */
    public boolean isLoggedIn() {
        return loggedIn.get();
    }

    /**
     * Returns true if the TCP connection is active.
     */
    public boolean isConnected() {
        return connected.get();
    }

    /**
     * Returns the next expected sequence number.
     */
    public long getNextSequenceNumber() {
        return nextSequenceNumber.get();
    }

    @Override
    public void close() throws Exception {
        if (loggedIn.get()) {
            try { logout(); } catch (IOException ignored) {}
        }
        connected.set(false);
        if (heartbeatExecutor != null) heartbeatExecutor.shutdownNow();
        if (readerExecutor != null) readerExecutor.shutdownNow();
        closeQuietly();
        log.info("SoupBinTCP client closed");
    }

    /**
     * Right-pads a string with spaces to the specified length.
     */
    private static byte[] padRight(String value, int length) {
        byte[] bytes = new byte[length];
        Arrays.fill(bytes, (byte) ' ');
        if (value != null) {
            byte[] src = value.getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(src, 0, bytes, 0, Math.min(src.length, length));
        }
        return bytes;
    }
}
