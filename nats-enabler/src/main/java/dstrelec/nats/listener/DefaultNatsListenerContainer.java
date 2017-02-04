/*
 * Copyright 2016-2017 the original author or authors.
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

package dstrelec.nats.listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.nats.client.AsyncSubscription;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.MessageHandler;

import dstrelec.nats.core.NatsConnectionFactory;
import dstrelec.nats.listener.config.ContainerProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;

/**
 * The Nats message listener container.
 *
 * @author Gary Russell
 * @author Murali Reddy
 * @author Marius Bogoevici
 * @author Martin Dam
 * @author Artem Bilan
 * @author Dario Strelec
 *
 */
public class DefaultNatsListenerContainer implements NatsListenerContainer, BeanNameAware, SmartLifecycle {

	private final Log logger = LogFactory.getLog(getClass());

	private final ContainerProperties containerProperties;

	private final Object lifecycleMonitor = new Object();

	private String beanName;

	private boolean autoStartup = true;

	private int phase = 0;

	private volatile boolean running = false;

	private final NatsConnectionFactory connectionFactory;

	private Connection connection;

	private MessageListener listener;

	private ErrorHandler errorHandler;

	private List<AsyncSubscription> subscriptions = new ArrayList<>();

	/**
	 * Construct an instance with the supplied configuration properties.
	 * @param connectionFactory the connection factory.
	 * @param containerProperties the container properties.
	 */
	public DefaultNatsListenerContainer(NatsConnectionFactory connectionFactory,
										ContainerProperties containerProperties) {
		Assert.notNull(containerProperties, "'containerProperties' cannot be null");
		Assert.notNull(connectionFactory, "A NatsConnectionFactory must be provided");

		if (containerProperties.getSubjects() != null) {
			this.containerProperties = new ContainerProperties(containerProperties.getSubjects());
		} else {
			this.containerProperties = new ContainerProperties();
		}

		BeanUtils.copyProperties(containerProperties, this.containerProperties,"subjects");
		this.connectionFactory = connectionFactory;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public boolean isAutoStartup() {
		return this.autoStartup;
	}

	public void setAutoStartup(boolean autoStartup) {
		this.autoStartup = autoStartup;
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	@Override
	public int getPhase() {
		return this.phase;
	}

	public ContainerProperties getContainerProperties() {
		return this.containerProperties;
	}

	@Override
	public void setupMessageListener(MessageListener messageListener) {
		this.containerProperties.setMessageListener(messageListener);
	}

	@Override
	public final void start() {
		synchronized (this.lifecycleMonitor) {
			Assert.isTrue(
					this.containerProperties.getMessageListener() instanceof MessageListener,
					"A " + MessageListener.class.getName() + " implementation must be provided");
			doStart();
		}
	}

	@Override
	public final void stop() {
		final CountDownLatch latch = new CountDownLatch(1);
		stop(new Runnable() {
			@Override
			public void run() {
				latch.countDown();
			}
		});
		try {
			latch.await(this.containerProperties.getShutdownTimeout(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			//
		}
	}

	@Override
	public void stop(Runnable callback) {
		synchronized (this.lifecycleMonitor) {
			doStop(callback);
		}
	}

	protected void doStart() {
		if (isRunning()) {
			return;
		}
		ContainerProperties containerProperties = getContainerProperties();

		this.subscriptions.clear();

		this.listener = containerProperties.getMessageListener();
		Assert.state(this.listener != null, "A MessageListener is required");

		try {
			final Connection connection = DefaultNatsListenerContainer.this.connectionFactory.getConnection();

			MessageHandler messageHandler = createMessageHandler();

			for (String subject : containerProperties.getSubjects()) {
				AsyncSubscription subscription = connection.subscribe(subject, messageHandler);
				subscriptions.add(subscription);
			}

			this.connection = connection;
			ErrorHandler errHandler = containerProperties.getErrorHandler();

			this.errorHandler = errHandler == null ? new LoggingErrorHandler() : errHandler;
			this.running = true;
		} catch (IOException e) {
			//TODO handle exception
			e.printStackTrace();
		}
	}

	protected void doStop(final Runnable callback) {
		if (isRunning()) {
			for (AsyncSubscription subscription : subscriptions) {
				//subscription.unsubscribe();
				if (logger.isDebugEnabled()) {
					logger.debug("Unsubscribed from subject " + subscription.getSubject());
				}
			}

			if (connection != null) {
				connection.close();
			}

			if (callback != null) {
				callback.run();
			}

			this.running = false;
		}
	}

	@Override
	public String toString() {
		return "DefaultNatsListenerContainer [id=" + beanName + "]";
	}


	public MessageHandler createMessageHandler() {
		return new MessageHandler() {

			@Override
			public void onMessage(Message message) {
				invokeMessageListener(message);
			}
		};
	}

	private void invokeMessageListener(final Message message) {
		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Processing " + message);
		}
		try {
			this.listener.onMessage(message);
		} catch (Exception e) {
			try {
				this.errorHandler.handle(e, message);
			} catch (Exception ee) {
				this.logger.error("Error handler threw an exception", ee);
			} catch (Error er) { //NOSONAR
				this.logger.error("Error handler threw an error", er);
				throw er;
			}
		}
	}

}