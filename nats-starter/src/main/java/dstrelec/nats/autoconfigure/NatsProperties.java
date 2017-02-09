/*
 * Copyright 2017 the original author or authors.
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

package dstrelec.nats.autoconfigure;

import java.util.List;

import io.nats.client.ConnectionFactory;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Nats.
 * <p>
 * Users should refer to Nats documentation for complete descriptions of these properties.
 *
 * @author Gary Russell
 * @author Stephane Nicoll
 * @author Artem Bilan
 * @author Dario Strelec
 */
@ConfigurationProperties("nats")
public class NatsProperties {

	private String host = ConnectionFactory.DEFAULT_HOST;

	private int port = ConnectionFactory.DEFAULT_PORT;

	private String username;

	private String password;

	private List<String> servers;

	private String connectionName;

	private boolean noRandomize;

	private boolean verbose;

	private boolean pedantic;

	private boolean secure;

	private boolean reconnectAllowed = true;

	private int maxReconnect = ConnectionFactory.DEFAULT_MAX_RECONNECT;

	private long reconnectWait = ConnectionFactory.DEFAULT_RECONNECT_WAIT;

	private long reconnectBufSize = ConnectionFactory.DEFAULT_RECONNECT_BUF_SIZE;

	private int connectionTimeout = ConnectionFactory.DEFAULT_TIMEOUT;

	private long pingInterval = ConnectionFactory.DEFAULT_PING_INTERVAL;

	private int maxPingsOut = ConnectionFactory.DEFAULT_MAX_PINGS_OUT;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getServers() {
		return servers;
	}

	public void setServers(List<String> servers) {
		this.servers = servers;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public boolean isNoRandomize() {
		return noRandomize;
	}

	public void setNoRandomize(boolean noRandomize) {
		this.noRandomize = noRandomize;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean isPedantic() {
		return pedantic;
	}

	public void setPedantic(boolean pedantic) {
		this.pedantic = pedantic;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public boolean isReconnectAllowed() {
		return reconnectAllowed;
	}

	public void setReconnectAllowed(boolean reconnectAllowed) {
		this.reconnectAllowed = reconnectAllowed;
	}

	public int getMaxReconnect() {
		return maxReconnect;
	}

	public void setMaxReconnect(int maxReconnect) {
		this.maxReconnect = maxReconnect;
	}

	public long getReconnectWait() {
		return reconnectWait;
	}

	public void setReconnectWait(long reconnectWait) {
		this.reconnectWait = reconnectWait;
	}

	public long getReconnectBufSize() {
		return reconnectBufSize;
	}

	public void setReconnectBufSize(int reconnectBufSize) {
		this.reconnectBufSize = reconnectBufSize;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public long getPingInterval() {
		return pingInterval;
	}

	public void setPingInterval(long pingInterval) {
		this.pingInterval = pingInterval;
	}

	public int getMaxPingsOut() {
		return maxPingsOut;
	}

	public void setMaxPingsOut(int maxPingsOut) {
		this.maxPingsOut = maxPingsOut;
	}

	private final Template template = new Template();

	public Template getTemplate() {
		return this.template;
	}

	public static class Template {

		/**
		 * Default subject to which messages will be sent.
		 */
		private String defaultSubject = "default";

		public String getDefaultSubject() {
			return this.defaultSubject;
		}

		public void setDefaultSubject(String defaultSubject) {
			this.defaultSubject = defaultSubject;
		}

	}

}