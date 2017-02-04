/*
 * Copyright 2016-2017 the original author or authors.
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

import io.nats.client.Message;

import dstrelec.nats.listener.MessageListener;

/**
 * A {@link MessageListener} adapter that implements filter logic
 * via a {@link MessageFilterStrategy}.
 *
 * @author Gary Russell
 * @author Dario Strelec
 *
 */
public class FilteringMessageListenerAdapter extends AbstractFilteringMessageListener implements MessageListener {

	/**
	 * Create an instance with the supplied strategy and delegate listener.
	 * @param delegate the delegate.
	 * @param messageFilterStrategy the filter.
	 */
	public FilteringMessageListenerAdapter(MessageListener delegate, MessageFilterStrategy messageFilterStrategy) {
		super(delegate, messageFilterStrategy);
	}

	@Override
	public void onMessage(Message message) {
		if (!filter(message)) {
			this.delegate.onMessage(message);
		}
	}

}