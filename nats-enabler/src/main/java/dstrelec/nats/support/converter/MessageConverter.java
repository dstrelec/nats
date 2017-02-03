/*
 * Copyright 2016 the original author or authors.
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

package dstrelec.nats.support.converter;

import java.lang.reflect.Type;

import org.springframework.messaging.Message;

/**
 * A Nats-specific {@link Message} converter strategy.
 *
 * @author Gary Russell
 * @author Dario Strelec
 *
 */
public interface MessageConverter {

	/**
	 * Convert a {@link io.nats.client.Message} to a {@link Message}.
	 * @param message the Nats message.
	 * @param payloadType the required payload type.
	 * @return the message.
	 */
	Message<?> toMessage(io.nats.client.Message message, Type payloadType);

	/**
	 * Convert a message to a Nats message.
	 * @param message the message.
	 * @param defaultSubject the default subject to use if no header found.
	 * @return the Nats message.
	 */
	io.nats.client.Message fromMessage(Message<?> message, String defaultSubject);

}