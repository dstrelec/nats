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
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.util.Assert;

/**
 * Helper bean for registering {@link NatsListenerEndpoint} with
 * a {@link NatsListenerEndpointRegistry}.
 *
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 * @author Artem Bilan
 * @author Gary Russell
 * @author Dario Strelec
 *
 * @see dstrelec.nats.annotation.NatsListenerConfigurer
 */
public class NatsListenerEndpointRegistrar implements BeanFactoryAware, InitializingBean {

	private final List<NatsListenerEndpointDescriptor> endpointDescriptors = new ArrayList<>();

	private NatsListenerEndpointRegistry endpointRegistry;

	private MessageHandlerMethodFactory messageHandlerMethodFactory;

	private NatsListenerContainerFactory containerFactory;

	private String containerFactoryBeanName;

	private BeanFactory beanFactory;

	private boolean startImmediately;

	/**
	 * Set the {@link NatsListenerEndpointRegistry} instance to use.
	 * @param endpointRegistry the {@link NatsListenerEndpointRegistry} instance to use.
	 */
	public void setEndpointRegistry(NatsListenerEndpointRegistry endpointRegistry) {
		this.endpointRegistry = endpointRegistry;
	}

	/**
	 * Return the {@link NatsListenerEndpointRegistry} instance for this
	 * registrar, may be {@code null}.
	 * @return the {@link NatsListenerEndpointRegistry} instance for this
	 * registrar, may be {@code null}.
	 */
	public NatsListenerEndpointRegistry getEndpointRegistry() {
		return this.endpointRegistry;
	}

	/**
	 * Set the {@link MessageHandlerMethodFactory} to use to configure the message
	 * listener responsible to serve an endpoint detected by this processor.
	 * <p>By default,
	 * {@link org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory}
	 * is used and it can be configured further to support additional method arguments
	 * or to customize conversion and validation support. See
	 * {@link org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory}
	 * javadoc for more details.
	 * @param natsHandlerMethodFactory the {@link MessageHandlerMethodFactory} instance.
	 */
	public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory natsHandlerMethodFactory) {
		this.messageHandlerMethodFactory = natsHandlerMethodFactory;
	}

	/**
	 * Return the custom {@link MessageHandlerMethodFactory} to use, if any.
	 * @return the custom {@link MessageHandlerMethodFactory} to use, if any.
	 */
	public MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
		return this.messageHandlerMethodFactory;
	}

	/**
	 * Set the {@link NatsListenerContainerFactory} to use in case a {@link NatsListenerEndpoint}
	 * is registered with a {@code null} container factory.
	 * <p>Alternatively, the bean name of the {@link NatsListenerContainerFactory} to use
	 * can be specified for a lazy lookup, see {@link #setContainerFactoryBeanName}.
	 * @param containerFactory the {@link NatsListenerContainerFactory} instance.
	 */
	public void setContainerFactory(NatsListenerContainerFactory containerFactory) {
		this.containerFactory = containerFactory;
	}

	/**
	 * Set the bean name of the {@link NatsListenerContainerFactory} to use in case
	 * a {@link NatsListenerEndpoint} is registered with a {@code null} container factory.
	 * Alternatively, the container factory instance can be registered directly:
	 * see {@link #setContainerFactory(NatsListenerContainerFactory)}.
	 * @param containerFactoryBeanName the {@link NatsListenerContainerFactory} bean name.
	 * @see #setBeanFactory
	 */
	public void setContainerFactoryBeanName(String containerFactoryBeanName) {
		this.containerFactoryBeanName = containerFactoryBeanName;
	}

	/**
	 * A {@link BeanFactory} only needs to be available in conjunction with
	 * {@link #setContainerFactoryBeanName}.
	 * @param beanFactory the {@link BeanFactory} instance.
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	@Override
	public void afterPropertiesSet() {
		registerAllEndpoints();
	}

	protected void registerAllEndpoints() {
		synchronized (this.endpointDescriptors) {
			for (NatsListenerEndpointDescriptor descriptor : this.endpointDescriptors) {
				this.endpointRegistry.registerListenerContainer(descriptor.endpoint, resolveContainerFactory(descriptor));
			}
			this.startImmediately = true;  // trigger immediate startup
		}
	}

	private NatsListenerContainerFactory resolveContainerFactory(NatsListenerEndpointDescriptor descriptor) {
		if (descriptor.containerFactory != null) {
			return descriptor.containerFactory;
		}
		else if (this.containerFactory != null) {
			return this.containerFactory;
		}
		else if (this.containerFactoryBeanName != null) {
			Assert.state(this.beanFactory != null, "BeanFactory must be set to obtain container factory by bean name");
			this.containerFactory = this.beanFactory.getBean(this.containerFactoryBeanName, NatsListenerContainerFactory.class);
			return this.containerFactory;  // Consider changing this if live change of the factory is required
		}
		else {
			throw new IllegalStateException("Could not resolve the " +
					NatsListenerContainerFactory.class.getSimpleName() + " to use for [" +
					descriptor.endpoint + "] no factory was given and no default is set.");
		}
	}

	/**
	 * Register a new {@link NatsListenerEndpoint} alongside the
	 * {@link NatsListenerContainerFactory} to use to create the underlying container.
	 * <p>The {@code factory} may be {@code null} if the default factory has to be
	 * used for that endpoint.
	 * @param endpoint the {@link NatsListenerEndpoint} instance to register.
	 * @param factory the {@link NatsListenerContainerFactory} to use.
	 */
	public void registerEndpoint(NatsListenerEndpoint endpoint, NatsListenerContainerFactory factory) {
		Assert.notNull(endpoint, "Endpoint must be set");
		Assert.hasText(endpoint.getId(), "Endpoint id must be set");
		// Factory may be null, we defer the resolution right before actually creating the container
		NatsListenerEndpointDescriptor descriptor = new NatsListenerEndpointDescriptor(endpoint, factory);
		synchronized (this.endpointDescriptors) {
			if (this.startImmediately) { // Register and start immediately
				this.endpointRegistry.registerListenerContainer(descriptor.endpoint,
						resolveContainerFactory(descriptor), true);
			}
			else {
				this.endpointDescriptors.add(descriptor);
			}
		}
	}

	/**
	 * Register a new {@link NatsListenerEndpoint} using the default
	 * {@link NatsListenerContainerFactory} to create the underlying container.
	 * @param endpoint the {@link NatsListenerEndpoint} instance to register.
	 * @see #setContainerFactory(NatsListenerContainerFactory)
	 * @see #registerEndpoint(NatsListenerEndpoint, NatsListenerContainerFactory)
	 */
	public void registerEndpoint(NatsListenerEndpoint endpoint) {
		registerEndpoint(endpoint, null);
	}


	private static final class NatsListenerEndpointDescriptor {

		private final NatsListenerEndpoint endpoint;

		private final NatsListenerContainerFactory containerFactory;

		private NatsListenerEndpointDescriptor(NatsListenerEndpoint endpoint, NatsListenerContainerFactory containerFactory) {
			this.endpoint = endpoint;
			this.containerFactory = containerFactory;
		}

	}

}