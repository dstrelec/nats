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


import dstrelec.nats.listener.DefaultNatsListenerContainer;
import dstrelec.nats.support.converter.MessageConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import dstrelec.nats.core.NatsConnectionFactory;
import dstrelec.nats.listener.adapter.MessageFilterStrategy;
import dstrelec.nats.listener.config.ContainerProperties;

import java.util.Collection;

/**
 * Base {@link NatsListenerContainerFactory} for Spring's base container implementation.
 *
 * @author Stephane Nicoll
 * @author Gary Russell
 * @author Artem Bilan
 * @author Dario Strelec
 *
 * @see DefaultNatsListenerContainer
 */
public class DefaultNatsListenerContainerFactory implements NatsListenerContainerFactory, ApplicationEventPublisherAware {

	private final ContainerProperties containerProperties = new ContainerProperties();

	private NatsConnectionFactory connectionFactory;

	private Boolean autoStartup;

	private Integer phase;

	private MessageConverter messageConverter;

	private MessageFilterStrategy messageFilterStrategy;

	private ApplicationEventPublisher applicationEventPublisher;

	/**
	 * Specify a {@link NatsConnectionFactory} to use.
	 * @param connectionFactory The consumer factory.
	 */
	public void setConnectionFactory(NatsConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public NatsConnectionFactory getConnectionFactory() {
		return this.connectionFactory;
	}

	/**
	 * Specify an {@code autoStartup boolean} flag.
	 * @param autoStartup true for auto startup.
	 * @see DefaultNatsListenerContainer#setAutoStartup(boolean)
	 */
	public void setAutoStartup(Boolean autoStartup) {
		this.autoStartup = autoStartup;
	}

	/**
	 * Specify a {@code phase} to use.
	 * @param phase The phase.
	 * @see DefaultNatsListenerContainer#setPhase(int)
	 */
	public void setPhase(int phase) {
		this.phase = phase;
	}

	/**
	 * Set the message converter to use if dynamic argument type matching is needed.
	 * @param messageConverter the converter.
	 */
	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	/**
	 * Set the message filter strategy.
	 * @param messageFilterStrategy the strategy.
	 */
	public void setMessageFilterStrategy(MessageFilterStrategy messageFilterStrategy) {
		this.messageFilterStrategy = messageFilterStrategy;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	/**
	 * Obtain the properties template for this factory - set properties as needed
	 * and they will be copied to a final properties instance for the endpoint.
	 * @return the properties.
	 */
	public ContainerProperties getContainerProperties() {
		return this.containerProperties;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DefaultNatsListenerContainer createListenerContainer(NatsListenerEndpoint endpoint) {
		DefaultNatsListenerContainer instance = createContainerInstance(endpoint);

		if (this.autoStartup != null) {
			instance.setAutoStartup(this.autoStartup);
		}
		if (this.phase != null) {
			instance.setPhase(this.phase);
		}
		if (endpoint.getId() != null) {
			instance.setBeanName(endpoint.getId());
		}

		if (endpoint instanceof AbstractNatsListenerEndpoint) {
			AbstractNatsListenerEndpoint aklEndpoint = (AbstractNatsListenerEndpoint) endpoint;
			if (this.messageFilterStrategy != null) {
				aklEndpoint.setMessageFilterStrategy(this.messageFilterStrategy);
			}
		}

		endpoint.setupListenerContainer(instance, this.messageConverter);
		initializeContainer(instance);

		return instance;
	}

	/**
	 * Create an empty container instance.
	 * @param endpoint the endpoint.
	 * @return the new container instance.
	 */
	protected DefaultNatsListenerContainer createContainerInstance(NatsListenerEndpoint endpoint) {
		Collection<String> subjects = endpoint.getSubjects();
		ContainerProperties properties = new ContainerProperties(subjects.toArray(new String[subjects.size()]));
		return new DefaultNatsListenerContainer(getConnectionFactory(), properties);
	}

	/**
	 * Further initialize the specified container.
	 * <p>Subclasses can inherit from this method to apply extra
	 * configuration if necessary.
	 * @param instance the container instance to configure.
	 */
	protected void initializeContainer(DefaultNatsListenerContainer instance) {
		ContainerProperties properties = instance.getContainerProperties();
		BeanUtils.copyProperties(this.containerProperties, properties, "subjects", "messageListener");
	}

}