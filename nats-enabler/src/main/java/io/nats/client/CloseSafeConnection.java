package io.nats.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CloseSafeConnection implements Connection {

	private final Connection delegate;

	public CloseSafeConnection(Connection delegate) {
		this.delegate = delegate;
	}

	public void closeDelegate() {
		delegate.close();
	}

	@Override
	public void publish(String s, byte[] bytes) throws IOException {
		delegate.publish(s, bytes);
	}

	@Override
	public void publish(Message message) throws IOException {
		delegate.publish(message);
	}

	@Override
	public void publish(String s, String s1, byte[] bytes) throws IOException {
		delegate.publish(s, s1, bytes);
	}

	@Override
	public void publish(String s, String s1, byte[] bytes, boolean b) throws IOException {
		delegate.publish(s, s1, bytes, b);
	}

	@Override
	public Message request(String s, byte[] bytes, long l) throws TimeoutException, IOException {
		return delegate.request(s, bytes, l);
	}

	@Override
	public Message request(String s, byte[] bytes) throws TimeoutException, IOException {
		return delegate.request(s, bytes);
	}

	@Override
	public Message request(String s, byte[] bytes, long l, TimeUnit timeUnit) throws TimeoutException, IOException {
		return delegate.request(s, bytes, l, timeUnit);
	}

	@Override
	public SyncSubscription subscribe(String s) {
		return delegate.subscribe(s);
	}

	@Override
	public SyncSubscription subscribe(String s, String s1) {
		return delegate.subscribe(s, s1);
	}

	@Override
	public AsyncSubscription subscribe(String s, MessageHandler messageHandler) {
		return delegate.subscribe(s, messageHandler);
	}

	@Override
	public AsyncSubscription subscribe(String s, String s1, MessageHandler messageHandler) {
		return delegate.subscribe(s, s1, messageHandler);
	}

	@Override
	public AsyncSubscription subscribeAsync(String s, MessageHandler messageHandler) {
		return delegate.subscribeAsync(s, messageHandler);
	}

	@Override
	public AsyncSubscription subscribeAsync(String s, String s1, MessageHandler messageHandler) {
		return delegate.subscribeAsync(s, s1, messageHandler);
	}

	@Override
	public SyncSubscription subscribeSync(String s, String s1) {
		return delegate.subscribeSync(s, s1);
	}

	@Override
	public SyncSubscription subscribeSync(String s) {
		return delegate.subscribeSync(s);
	}

	@Override
	public String newInbox() {
		return delegate.newInbox();
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public boolean isClosed() {
		return delegate.isClosed();
	}

	@Override
	public boolean isConnected() {
		return delegate.isConnected();
	}

	@Override
	public boolean isReconnecting() {
		return delegate.isReconnecting();
	}

	@Override
	public Statistics getStats() {
		return delegate.getStats();
	}

	@Override
	public void resetStats() {
		delegate.resetStats();;
	}

	@Override
	public long getMaxPayload() {
		return delegate.getMaxPayload();
	}

	@Override
	public void flush(int i) throws IOException, TimeoutException, Exception {
		delegate.flush(i);
	}

	@Override
	public void flush() throws IOException, Exception {
		delegate.flush();
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		return delegate.getExceptionHandler();
	}

	@Override
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		delegate.setExceptionHandler(exceptionHandler);
	}

	@Override
	public ClosedCallback getClosedCallback() {
		return delegate.getClosedCallback();
	}

	@Override
	public void setClosedCallback(ClosedCallback closedCallback) {
		delegate.setClosedCallback(closedCallback);
	}

	@Override
	public DisconnectedCallback getDisconnectedCallback() {
		return delegate.getDisconnectedCallback();
	}

	@Override
	public void setDisconnectedCallback(DisconnectedCallback disconnectedCallback) {
		delegate.setDisconnectedCallback(disconnectedCallback);
	}

	@Override
	public ReconnectedCallback getReconnectedCallback() {
		return delegate.getReconnectedCallback();
	}

	@Override
	public void setReconnectedCallback(ReconnectedCallback reconnectedCallback) {
		delegate.setReconnectedCallback(reconnectedCallback);
	}

	@Override
	public String getConnectedUrl() {
		return delegate.getConnectedUrl();
	}

	@Override
	public String getConnectedServerId() {
		return delegate.getConnectedServerId();
	}

	@Override
	public Constants.ConnState getState() {
		return delegate.getState();
	}

	@Override
	public ServerInfo getConnectedServerInfo() {
		return delegate.getConnectedServerInfo();
	}

	@Override
	public Exception getLastException() {
		return delegate.getLastException();
	}

	@Override
	public int getPendingByteCount() {
		return delegate.getPendingByteCount();
	}

}
