package com.github.fridujo.rabbitmq.mock;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.BlockedCallback;
import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ExceptionHandler;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.UnblockedCallback;
import com.rabbitmq.client.impl.AMQConnection;
import com.rabbitmq.client.impl.DefaultExceptionHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MockConnection implements Connection {

    private final AtomicBoolean opened = new AtomicBoolean(true);
    private final AtomicInteger channelSequence = new AtomicInteger();
    private final MockNode mockNode;
    private final InetAddress address;
    private final DefaultExceptionHandler exceptionHandler = new DefaultExceptionHandler();
    private String id;

    public MockConnection(MockNode mockNode) {
        this.mockNode = mockNode;
        address = new InetSocketAddress("127.0.0.1", 0).getAddress();
    }

    @Override
    public InetAddress getAddress() {
        return address;
    }

    @Override
    public int getPort() {
        return com.rabbitmq.client.ConnectionFactory.DEFAULT_AMQP_PORT;
    }

    @Override
    public int getChannelMax() {
        return 0;
    }

    @Override
    public int getFrameMax() {
        return 0;
    }

    @Override
    public int getHeartbeat() {
        return 0;
    }

    @Override
    public Map<String, Object> getClientProperties() {
        return AMQConnection.defaultClientProperties();
    }

    @Override
    public String getClientProvidedName() {
        return null;
    }

    @Override
    public Map<String, Object> getServerProperties() {
        return Collections.emptyMap();
    }

    @Override
    public Channel createChannel() throws AlreadyClosedException {
        if (!isOpen()) {
            throw new AlreadyClosedException(new ShutdownSignalException(false, true, null, this));
        }
        return createChannel(channelSequence.incrementAndGet());
    }

    @Override
    public Channel createChannel(int channelNumber) {
        MockChannel mockChannel = new MockChannel(channelNumber, mockNode, this);
        return mockChannel;
    }

    @Override
    public void close() {
        close(AMQP.REPLY_SUCCESS, "OK");
    }

    @Override
    public void close(int closeCode, String closeMessage) {
        close(closeCode, closeMessage, -1);
    }

    @Override
    public void close(int timeout) {
        close(AMQP.REPLY_SUCCESS, "OK", timeout);
    }

    @Override
    public void close(int closeCode, String closeMessage, int timeout) {
        opened.set(false);
    }

    @Override
    public void abort() {
        abort(AMQP.REPLY_SUCCESS, "OK");
    }

    @Override
    public void abort(int closeCode, String closeMessage) {
        abort(closeCode, closeMessage, -1);
    }

    @Override
    public void abort(int timeout) {
        abort(AMQP.REPLY_SUCCESS, "OK", timeout);
    }

    @Override
    public void abort(int closeCode, String closeMessage, int timeout) {
        close(closeCode, closeMessage, timeout);
    }

    @Override
    public void addBlockedListener(BlockedListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockedListener addBlockedListener(BlockedCallback blockedCallback, UnblockedCallback unblockedCallback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeBlockedListener(BlockedListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearBlockedListeners() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void addShutdownListener(ShutdownListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeShutdownListener(ShutdownListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ShutdownSignalException getCloseReason() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notifyListeners() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOpen() {
        return opened.get();
    }
}
