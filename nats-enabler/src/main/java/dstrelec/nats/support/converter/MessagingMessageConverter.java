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
import java.util.Map;

import dstrelec.nats.support.NatsHeaders;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.SerializationUtils;

/**
 * A Messaging {@link MessageConverter} implementation for a message listener that receives individual messages.
 * <p>
 * Populates {@link NatsHeaders} based on the {@link io.nats.client.Message} onto the returned message.
 *
 * @author Marius Bogoevici
 * @author Gary Russell
 * @author Dariusz Szablinski
 * @author Dario Strelec
 *
 */
public class MessagingMessageConverter implements MessageConverter {

	private boolean generateMessageId = false;

	private boolean generateTimestamp = false;

	/**
	 * Generate {@link Message} {@code ids} for produced messages. If set to {@code false},
	 * will try to use a default value. By default set to {@code false}.
	 * @param generateMessageId true if a message id should be generated
	 */
	public void setGenerateMessageId(boolean generateMessageId) {
		this.generateMessageId = generateMessageId;
	}

	/**
	 * Generate {@code timestamp} for produced messages. If set to {@code false}, -1 is
	 * used instead. By default set to {@code false}.
	 * @param generateTimestamp true if a timestamp should be generated
	 */
	public void setGenerateTimestamp(boolean generateTimestamp) {
		this.generateTimestamp = generateTimestamp;
	}

	@Override
	public Message<?> toMessage(io.nats.client.Message message, Type type) {
		NatsMessageHeaders natsMessageHeaders = new NatsMessageHeaders(this.generateMessageId, this.generateTimestamp);

		Map<String, Object> rawHeaders = natsMessageHeaders.getRawHeaders();
		rawHeaders.put(NatsHeaders.SUBJECT, message.getSubject());
		rawHeaders.put(NatsHeaders.REPLY_TO, message.getReplyTo());

		return MessageBuilder.createMessage(extractAndConvertValue(message, type), natsMessageHeaders);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public io.nats.client.Message fromMessage(Message<?> message, String defaultSubject) {
		MessageHeaders headers = message.getHeaders();
		String subject = headers.get(NatsHeaders.SUBJECT, String.class);
		String replyTo = headers.get(NatsHeaders.REPLY_TO, String.class);
		byte[] payload = convertPayload(message);

		return new io.nats.client.Message(subject == null ? defaultSubject : subject, replyTo, payload);
	}

	/**
	 * Subclasses can convert the payload; by default, it's sent unchanged to Nats.
	 * @param message the message.
	 * @return the payload.
	 */
	protected byte[] convertPayload(Message<?> message) {
		Object payload = message.getPayload();
		if (payload != null && payload instanceof byte[]) {
			return (byte[]) message.getPayload();
		}

		return SerializationUtils.serialize(payload);
	}

	/**
	 * Subclasses can convert the value; by default, it's returned as provided by Nats.
	 * @param message the Nats message.
	 * @param type the required type.
	 * @return the value.
	 */
	protected Object extractAndConvertValue(io.nats.client.Message message, Type type) {
		return message.getData();
	}

}