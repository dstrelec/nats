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

import dstrelec.nats.annotation.EnableNats;
import dstrelec.nats.config.DefaultNatsListenerContainerFactory;
import dstrelec.nats.config.NatsListenerConfigUtils;
import dstrelec.nats.core.NatsConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Nats annotation-driven support.
 *
 * @author Gary Russell
 * @author Dario Strelec
 */
@Configuration
@ConditionalOnClass(EnableNats.class)
class NatsAnnotationDrivenConfiguration {

	private final NatsProperties properties;

	NatsAnnotationDrivenConfiguration(NatsProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean
	public DefaultNatsListenerContainerFactoryConfigurer natsListenerContainerFactoryConfigurer() {
		DefaultNatsListenerContainerFactoryConfigurer configurer = new DefaultNatsListenerContainerFactoryConfigurer();
		configurer.setNatsProperties(this.properties);
		return configurer;
	}

	@Bean
	@ConditionalOnMissingBean(name = "natsListenerContainerFactory")
	public DefaultNatsListenerContainerFactory natsListenerContainerFactory(
			DefaultNatsListenerContainerFactoryConfigurer configurer,
			NatsConnectionFactory natsConnectionFactory) {
		DefaultNatsListenerContainerFactory factory = new DefaultNatsListenerContainerFactory();
		configurer.configure(factory, natsConnectionFactory);
		return factory;
	}

	@EnableNats
	@ConditionalOnMissingBean(name = NatsListenerConfigUtils.NATS_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
	protected static class EnableNatsConfiguration {

	}

}