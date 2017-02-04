/*
 * Copyright 2002-2017 the original author or authors.
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


import dstrelec.nats.config.NatsListenerContainerFactory;
import dstrelec.nats.config.NatsListenerEndpointRegistrar;

/**
 * Optional interface to be implemented by Spring managed bean willing
 * to customize how Nats listener endpoints are configured. Typically
 * used to defined the default {@link NatsListenerContainerFactory
 * NatsListenerContainerFactory} to use or for registering Nats endpoints
 * in a <em>programmatic</em> fashion as opposed to the <em>declarative</em>
 * approach of using the @{@link NatsListener} annotation.
 *
 * <p>See @{@link EnableNats} for detailed usage examples.
 *
 * @author Stephane Nicoll
 * @author Dario Strelec
 *
 * @see EnableNats
 * @see dstrelec.nats.config.NatsListenerEndpointRegistrar
 */
public interface NatsListenerConfigurer {

	/**
	 * Callback allowing a {@link dstrelec.nats.config.NatsListenerEndpointRegistry NatsListenerEndpointRegistry}
	 * and specific {@link dstrelec.nats.config.NatsListenerEndpoint NatsListenerEndpoint} instances to be registered
	 * against the given {@link NatsListenerEndpointRegistrar}. The default
	 * {@link NatsListenerContainerFactory NatsListenerContainerFactory} can also be customized.
	 * @param registrar the registrar to be configured
	 */
	void configureNatsListeners(NatsListenerEndpointRegistrar registrar);

}