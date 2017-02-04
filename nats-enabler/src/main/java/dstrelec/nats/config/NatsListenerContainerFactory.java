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

package dstrelec.nats.config;

import dstrelec.nats.listener.NatsListenerContainer;

/**
 * Factory of {@link NatsListenerContainer} based on a
 * {@link NatsListenerEndpoint} definition.
 *
 * @author Stephane Nicoll
 * @author Dario Strelec
 *
 * @see NatsListenerEndpoint
 */
public interface NatsListenerContainerFactory {

	/**
	 * Create a {@link NatsListenerContainer} for the given {@link NatsListenerEndpoint}.
	 * @param endpoint the endpoint to configure
	 * @return the created container
	 */
	NatsListenerContainer createListenerContainer(NatsListenerEndpoint endpoint);

}