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

package dstrelec.nats.core;

import org.springframework.messaging.Message;

/**
 * The basic Nats operations contract.
 *
 * @author Dario Strelec
 *
 */
public interface NatsOperations {

	/**
	 * Publish the data to the default subject.
	 * @param data The data.
	 */
	void publish(Object data);

	/**
	 * Publish the data to the provided subject.
	 * @param subject the subject.
	 * @param data the data.
	 */
	void publish(String subject, Object data);

	/**
	 * Publish a message with routing information in message headers. The message payload
	 * may be converted before sending.
	 * @param message the message to send.
	 * @see dstrelec.nats.support.NatsHeaders#SUBJECT
	 */
	void publishMessage(Message<?> message);

}
