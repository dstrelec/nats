/*
 * Copyright 2014-2016 the original author or authors.
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

package dstrelec.nats.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import dstrelec.nats.listener.NatsListenerContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Creates the necessary {@link NatsListenerContainer} instances for the
 * registered {@linkplain NatsListenerEndpoint endpoints}. Also manages the
 * lifecycle of the listener containers, in particular within the lifecycle
 * of the application context.
 *
 * <p>Contrary to {@link NatsListenerContainer}s created manually, listener
 * containers managed by registry are not beans in the application context and
 * are not candidates for autowiring. Use {@link #getListenerContainers()} if
 * you need to access this registry's listener containers for management purposes.
 * If you need to access to a specific message listener container, use
 * {@link #getListenerContainer(String)} with the id of the endpoint.
 *
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 * @author Artem Bilan
 * @author Gary Russell
 * @author Dario Strelec
 *
 * @see NatsListenerEndpoint
 * @see NatsListenerContainer
 * @see NatsListenerContainerFactory
 */
public class NatsListenerEndpointRegistry implements DisposableBean, SmartLifecycle, ApplicationContextAware,
		ApplicationListener<ContextRefreshedEvent> {

	protected final Log logger = LogFactory.getLog(getClass()); //NOSONAR

	private final Map<String, NatsListenerContainer> listenerContainers = new ConcurrentHashMap<>();

	private int phase = Integer.MAX_VALUE;

	private ConfigurableApplicationContext applicationContext;

	private boolean contextRefreshed;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (applicationContext instanceof ConfigurableApplicationContext) {
			this.applicationContext = (ConfigurableApplicationContext) applicationContext;
		}
	}

	/**
	 * Return the {@link NatsListenerContainer} with the specified id or
	 * {@code null} if no such container exists.
	 * @param id the id of the container
	 * @return the container or {@code null} if no container with that id exists
	 * @see NatsListenerEndpoint#getId()
	 * @see #getListenerContainerIds()
	 */
	public NatsListenerContainer getListenerContainer(String id) {
		Assert.hasText(id, "Container identifier must not be empty");
		return this.listenerContainers.get(id);
	}

	/**
	 * Return the ids of the managed {@link NatsListenerContainer} instance(s).
	 * @return the ids.
	 * @see #getListenerContainer(String)
	 */
	public Set<String> getListenerContainerIds() {
		return Collections.unmodifiableSet(this.listenerContainers.keySet());
	}

	/**
	 * Return the managed {@link NatsListenerContainer} instance(s).
	 * @return the managed {@link NatsListenerContainer} instance(s).
	 */
	public Collection<NatsListenerContainer> getListenerContainers() {
		return Collections.unmodifiableCollection(this.listenerContainers.values());
	}

	/**
	 * Create a message listener container for the given {@link NatsListenerEndpoint}.
	 * <p>This create the necessary infrastructure to honor that endpoint
	 * with regards to its configuration.
	 * @param endpoint the endpoint to add
	 * @param factory the listener factory to use
	 * @see #registerListenerContainer(NatsListenerEndpoint, NatsListenerContainerFactory, boolean)
	 */
	public void registerListenerContainer(NatsListenerEndpoint endpoint, NatsListenerContainerFactory factory) {
		registerListenerContainer(endpoint, factory, false);
	}

	/**
	 * Create a message listener container for the given {@link NatsListenerEndpoint}.
	 * <p>This create the necessary infrastructure to honor that endpoint
	 * with regards to its configuration.
	 * <p>The {@code startImmediately} flag determines if the container should be
	 * started immediately.
	 * @param endpoint the endpoint to add.
	 * @param factory the {@link NatsListenerContainerFactory} to use.
	 * @param startImmediately start the container immediately if necessary
	 * @see #getListenerContainers()
	 * @see #getListenerContainer(String)
	 */
	@SuppressWarnings("unchecked")
	public void registerListenerContainer(NatsListenerEndpoint endpoint, NatsListenerContainerFactory factory,
										  boolean startImmediately) {
		Assert.notNull(endpoint, "Endpoint must not be null");
		Assert.notNull(factory, "Factory must not be null");

		String id = endpoint.getId();
		Assert.hasText(id, "Endpoint id must not be empty");
		synchronized (this.listenerContainers) {
			Assert.state(!this.listenerContainers.containsKey(id),
					"Another endpoint is already registered with id '" + id + "'");
			NatsListenerContainer container = createListenerContainer(endpoint, factory);
			this.listenerContainers.put(id, container);
			if (StringUtils.hasText(endpoint.getGroup()) && this.applicationContext != null) {
				List<NatsListenerContainer> containerGroup;
				if (this.applicationContext.containsBean(endpoint.getGroup())) {
					containerGroup = this.applicationContext.getBean(endpoint.getGroup(), List.class);
				}
				else {
					containerGroup = new ArrayList<NatsListenerContainer>();
					this.applicationContext.getBeanFactory().registerSingleton(endpoint.getGroup(), containerGroup);
				}
				containerGroup.add(container);
			}
			if (startImmediately) {
				startIfNecessary(container);
			}
		}
	}

	/**
	 * Create and start a new {@link NatsListenerContainer} using the specified factory.
	 * @param endpoint the endpoint to create a {@link NatsListenerContainer}.
	 * @param factory the {@link NatsListenerContainerFactory} to use.
	 * @return the {@link NatsListenerContainer}.
	 */
	protected NatsListenerContainer createListenerContainer(NatsListenerEndpoint endpoint,
															NatsListenerContainerFactory factory) {

		NatsListenerContainer listenerContainer = factory.createListenerContainer(endpoint);

		if (listenerContainer instanceof InitializingBean) {
			try {
				((InitializingBean) listenerContainer).afterPropertiesSet();
			}
			catch (Exception ex) {
				throw new BeanInitializationException("Failed to initialize message listener container", ex);
			}
		}

		int containerPhase = listenerContainer.getPhase();
		if (containerPhase < Integer.MAX_VALUE) {  // a custom phase value
			if (this.phase < Integer.MAX_VALUE && this.phase != containerPhase) {
				throw new IllegalStateException("Encountered phase mismatch between container factory definitions: " +
						this.phase + " vs " + containerPhase);
			}
			this.phase = listenerContainer.getPhase();
		}

		return listenerContainer;
	}


	@Override
	public void destroy() {
		for (NatsListenerContainer listenerContainer : getListenerContainers()) {
			if (listenerContainer instanceof DisposableBean) {
				try {
					((DisposableBean) listenerContainer).destroy();
				}
				catch (Exception ex) {
					this.logger.warn("Failed to destroy message listener container", ex);
				}
			}
		}
	}


	// Delegating implementation of SmartLifecycle

	@Override
	public int getPhase() {
		return this.phase;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void start() {
		for (NatsListenerContainer listenerContainer : getListenerContainers()) {
			startIfNecessary(listenerContainer);
		}
	}

	@Override
	public void stop() {
		for (NatsListenerContainer listenerContainer : getListenerContainers()) {
			listenerContainer.stop();
		}
	}

	@Override
	public void stop(Runnable callback) {
		Collection<NatsListenerContainer> listenerContainers = getListenerContainers();
		AggregatingCallback aggregatingCallback = new AggregatingCallback(listenerContainers.size(), callback);
		for (NatsListenerContainer listenerContainer : listenerContainers) {
			if (listenerContainer.isRunning()) {
				listenerContainer.stop(aggregatingCallback);
			}
			else {
				aggregatingCallback.run();
			}
		}
	}

	@Override
	public boolean isRunning() {
		for (NatsListenerContainer listenerContainer : getListenerContainers()) {
			if (listenerContainer.isRunning()) {
				return true;
			}
		}
		return false;
	}


	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().equals(this.applicationContext)) {
			this.contextRefreshed = true;
		}
	}

	/**
	 * Start the specified {@link NatsListenerContainer} if it should be started
	 * on startup.
	 * @param listenerContainer the listener container to start.
	 * @see NatsListenerContainer#isAutoStartup()
	 */
	private void startIfNecessary(NatsListenerContainer listenerContainer) {
		if (this.contextRefreshed || listenerContainer.isAutoStartup()) {
			listenerContainer.start();
		}
	}


	private static final class AggregatingCallback implements Runnable {

		private final AtomicInteger count;

		private final Runnable finishCallback;

		private AggregatingCallback(int count, Runnable finishCallback) {
			this.count = new AtomicInteger(count);
			this.finishCallback = finishCallback;
		}

		@Override
		public void run() {
			if (this.count.decrementAndGet() <= 0) {
				this.finishCallback.run();
			}
		}

	}

}