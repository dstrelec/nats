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

package dstrelec.nats.listener.adapter;

import dstrelec.nats.listener.MessageListener;
import io.nats.client.Message;

import org.springframework.util.Assert;

/**
 * An abstract message listener adapter that implements record filter logic
 * via a {@link MessageFilterStrategy}.
 *
 * @author Gary Russell
 * @author Dario Strelec
 *
 */
public abstract class AbstractFilteringMessageListener {

	protected final MessageListener delegate;

	private final MessageFilterStrategy messageFilterStrategy;

	protected AbstractFilteringMessageListener(MessageListener delegate, MessageFilterStrategy messageFilterStrategy) {
		Assert.notNull(messageFilterStrategy, "'messageFilterStrategy' cannot be null");
		this.delegate = delegate;
		this.messageFilterStrategy = messageFilterStrategy;
	}

	protected boolean filter(Message message) {
		return this.messageFilterStrategy.filter(message);
	}

}