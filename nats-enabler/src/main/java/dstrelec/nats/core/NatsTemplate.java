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

import dstrelec.nats.NatsException;
import dstrelec.nats.support.converter.MessageConverter;
import dstrelec.nats.support.converter.MessagingMessageConverter;
import io.nats.client.Connection;
import org.springframework.messaging.Message;

import java.io.IOException;

/**
 * A template for executing high-level operations.
 *
 * @author Dario Strelec
 */
public class NatsTemplate implements NatsOperations {

	private static final String DEFAULT_SUBJECT = "default";

	private NatsConnectionFactory connectionFactory;

	private MessageConverter messageConverter = new MessagingMessageConverter();

	private volatile String defaultSubject = DEFAULT_SUBJECT;

	/**
	 * Create an instance using the supplied connection factory.
	 * @param connectionFactory the connection factory.
	 */
	public NatsTemplate(NatsConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	/**
	 * The default subject for send methods where a subject is not providing.
	 * @return the subject.
	 */
	public String getDefaultSubject() {
		return this.defaultSubject;
	}

	/**
	 * Set the default subject for send methods where a subject is not providing.
	 * @param defaultSubject the subject.
	 */
	public void setDefaultSubject(String defaultSubject) {
		this.defaultSubject = defaultSubject;
	}

	/**
	 * Return the message converter.
	 * @return the message converter.
	 */
	public MessageConverter getMessageConverter() {
		return this.messageConverter;
	}

	/**
	 * Set the message converter to use.
	 * @param messageConverter the message converter.
	 */
	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	@Override
	public void publish(Object data) {
		publish(this.defaultSubject, data);
	}

	@Override
	public void publish(String subject, Object data) {
		try {
			getConnection().publish(subject, data.toString().getBytes());
		} catch (IOException e) {
			throw new NatsException("Publish failed.", e);
		}
	}

	@Override
	public void publishMessage(Message<?> message) {
		io.nats.client.Message msg = this.messageConverter.fromMessage(message, defaultSubject);
		try {
			getConnection().publish(msg);
		} catch (IOException e) {
			throw new NatsException("Publish failed.", e);
		}
	}

	private Connection getConnection() throws NatsException {
		try {
			return connectionFactory.getConnection();
		} catch (IOException e) {
			throw new NatsException("Connection failed.", e);
		}
	}
}
