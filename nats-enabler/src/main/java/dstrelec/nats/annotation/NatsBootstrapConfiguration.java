/*
 * Copyright 2002-2016 the original author or authors.
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

package dstrelec.nats.annotation;

import dstrelec.nats.config.NatsListenerConfigUtils;
import dstrelec.nats.config.NatsListenerEndpointRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * {@code @Configuration} class that registers a {@link NatsListenerAnnotationBeanPostProcessor}
 * bean capable of processing Spring's @{@link NatsListener} annotation. Also register
 * a default {@link NatsListenerEndpointRegistry}.
 *
 * <p>This configuration class is automatically imported when using the @{@link EnableNats}
 * annotation.  See {@link EnableNats} Javadoc for complete usage.
 *
 * @author Stephane Nicoll
 * @author Gary Russell
 * @author Dario Strelec
 *
 * @see NatsListenerAnnotationBeanPostProcessor
 * @see NatsListenerEndpointRegistry
 * @see EnableNats
 */
@Configuration
public class NatsBootstrapConfiguration {

	@SuppressWarnings("rawtypes")
	@Bean(name = NatsListenerConfigUtils.NATS_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public NatsListenerAnnotationBeanPostProcessor natsListenerAnnotationProcessor() {
		return new NatsListenerAnnotationBeanPostProcessor();
	}

	@Bean(name = NatsListenerConfigUtils.NATS_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME)
	public NatsListenerEndpointRegistry defaultNatsListenerEndpointRegistry() {
		return new NatsListenerEndpointRegistry();
	}

}