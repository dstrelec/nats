/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dstrelec.nats.core;

import dstrelec.nats.NatsException;
import io.nats.client.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.Lifecycle;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * The {@link NatsConnectionFactory} implementation for the {@code singleton} shared {@link Connection}
 * instance.
 * <p>
 * This implementation will create a single {@link Connection} instance for provided {@code configs}.
 * <p>
 * The {@link Connection} instance is freed from the external {@link Connection#close()} invocation
 * with the internal wrapper. The real {@link Connection#close()} is called on the target
 * {@link Connection} during the {@link Lifecycle#stop()} or {@link DisposableBean#destroy()}.
 *
 * @author Gary Russell
 * @author Murali Reddy
 * @author Dario Strelec
 */
public class DefaultConnectionFactory implements NatsConnectionFactory, Lifecycle, DisposableBean {

	private static final Log logger = LogFactory.getLog(DefaultConnectionFactory.class);

	private ConnectionFactory connectionFactory = new ConnectionFactory();

	private volatile CloseSafeConnection connection;

	private volatile boolean running;

	@Override
	public void destroy() throws Exception {
		CloseSafeConnection connection = this.connection;
		this.connection = null;
		if (connection != null) {
			connection.closeDelegate();
		}
	}


	@Override
	public void start() {
		this.running = true;
	}


	@Override
	public void stop() {
		try {
			destroy();
		} catch (Exception e) {
			logger.error("Exception while stopping producer", e);
		} finally {
			this.running = false;
		}
	}


	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public Connection getConnection() throws IOException {
		if (connection == null) {
			synchronized (this) {
				if (connection == null) {
					try {
						connection = new CloseSafeConnection(connectionFactory.createConnection());
					} catch (TimeoutException e) {
						throw new NatsException("Failed to connect.", e);
					}
				}
			}
		}

		return connection;
	}

	public String getHost() {
		return connectionFactory.getHost();
	}

	public void setHost(String host) {
		connectionFactory.setHost(host);
	}

	public int getPort() {
		return connectionFactory.getPort();
	}

	public void setPort(int port) {
		connectionFactory.setPort(port);
	}

	public String getUsername() {
		return connectionFactory.getUsername();
	}

	public void setUsername(String username) {
		connectionFactory.setUsername(username);
	}

	public String getPassword() {
		return connectionFactory.getPassword();
	}

	public void setPassword(String password) {
		connectionFactory.setPassword(password);
	}

	public List<URI> getServers() {
		return connectionFactory.getServers();
	}

	public void setServers(List<URI> servers) {
		connectionFactory.setServers(servers);
	}

	public void setServers(String[] servers) {
		connectionFactory.setServers(servers);
	}

	public String getConnectionName() {
		return connectionFactory.getConnectionName();
	}

	public void setConnectionName(String connectionName) {
		connectionFactory.setConnectionName(connectionName);
	}

	public boolean isNoRandomize() {
		return connectionFactory.isNoRandomize();
	}

	public void setNoRandomize(boolean noRandomize) {
		connectionFactory.setNoRandomize(noRandomize);
	}

	public boolean isVerbose() {
		return connectionFactory.isVerbose();
	}

	public void setVerbose(boolean verbose) {
		connectionFactory.setVerbose(verbose);
	}

	public boolean isPedantic() {
		return connectionFactory.isPedantic();
	}

	public void setPedantic(boolean pedantic) {
		connectionFactory.setPedantic(pedantic);
	}

	public boolean isSecure() {
		return connectionFactory.isSecure();
	}

	public void setSecure(boolean secure) {
		connectionFactory.setSecure(secure);
	}

	public boolean isReconnectAllowed() {
		return connectionFactory.isReconnectAllowed();
	}

	public void setReconnectAllowed(boolean reconnectAllowed) {
		connectionFactory.setReconnectAllowed(reconnectAllowed);
	}

	public int getMaxReconnect() {
		return connectionFactory.getMaxReconnect();
	}

	public void setMaxReconnect(int maxReconnect) {
		connectionFactory.setMaxReconnect(maxReconnect);
	}

	public long getReconnectWait() {
		return connectionFactory.getReconnectWait();
	}

	public void setReconnectWait(long reconnectWait) {
		connectionFactory.setReconnectWait(reconnectWait);
	}

	public long getReconnectBufSize() {
		return connectionFactory.getReconnectBufSize();
	}

	public void setReconnectBufSize(int reconnectBufSize) {
		connectionFactory.setReconnectBufSize(reconnectBufSize);
	}

	public int getConnectionTimeout() {
		return connectionFactory.getConnectionTimeout();
	}

	public void setConnectionTimeout(int connectionTimeout) {
		connectionFactory.setConnectionTimeout(connectionTimeout);
	}

	public long getPingInterval() {
		return connectionFactory.getPingInterval();
	}

	public void setPingInterval(long pingInterval) {
		connectionFactory.setPingInterval(pingInterval);
	}

	public int getMaxPingsOut() {
		return connectionFactory.getMaxPingsOut();
	}

	public void setMaxPingsOut(int maxPingsOut) {
		connectionFactory.setMaxPingsOut(maxPingsOut);
	}

}
