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

import dstrelec.nats.config.DefaultNatsListenerContainerFactory;
import dstrelec.nats.core.NatsConnectionFactory;

/**
 * Configure {@link DefaultNatsListenerContainerFactory} with sensible defaults.
 *
 * @author Gary Russell
 * @author Dario Strelec
 */
public class DefaultNatsListenerContainerFactoryConfigurer {

	private NatsProperties properties;

	/**
	 * Set the {@link NatsProperties} to use.
	 * @param properties the properties
	 */
	void setNatsProperties(NatsProperties properties) {
		this.properties = properties;
	}

	/**
	 * Configure the specified Nats listener container factory. The factory can be
	 * further tuned and default settings can be overridden.
	 * @param listenerContainerFactory the {@link DefaultNatsListenerContainerFactory}
	 * instance to configure
	 * @param connectionFactory the {@link NatsConnectionFactory} to use
	 */
	public void configure(
			DefaultNatsListenerContainerFactory listenerContainerFactory,
			NatsConnectionFactory connectionFactory) {
		listenerContainerFactory.setConnectionFactory(connectionFactory);
	}

}