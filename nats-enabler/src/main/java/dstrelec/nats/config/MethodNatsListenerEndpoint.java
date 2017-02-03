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

package dstrelec.nats.config;

import java.lang.reflect.Method;

import dstrelec.nats.listener.NatsListenerContainer;
import dstrelec.nats.listener.adapter.HandlerAdapter;
import dstrelec.nats.listener.adapter.MessagingMessageListenerAdapter;
import dstrelec.nats.support.converter.MessageConverter;
import dstrelec.nats.support.converter.MessagingMessageConverter;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.util.Assert;

/**
 * A {@link NatsListenerEndpoint} providing the method to invoke to process
 * an incoming message for this endpoint.
 *
 * @author Stephane Nicoll
 * @author Artem Bilan
 * @author Gary Russell
 * @author Dario Strelec
 */
public class MethodNatsListenerEndpoint extends AbstractNatsListenerEndpoint {

	private Object bean;

	private Method method;

	private MessageHandlerMethodFactory messageHandlerMethodFactory;


	/**
	 * Set the object instance that should manage this endpoint.
	 * @param bean the target bean instance.
	 */
	public void setBean(Object bean) {
		this.bean = bean;
	}

	public Object getBean() {
		return this.bean;
	}

	/**
	 * Set the method to invoke to process a message managed by this endpoint.
	 * @param method the target method for the {@link #bean}.
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	public Method getMethod() {
		return this.method;
	}

	/**
	 * Set the {@link MessageHandlerMethodFactory} to use to build the
	 * {@link InvocableHandlerMethod} responsible to manage the invocation
	 * of this endpoint.
	 * @param messageHandlerMethodFactory the {@link MessageHandlerMethodFactory} instance.
	 */
	public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory messageHandlerMethodFactory) {
		this.messageHandlerMethodFactory = messageHandlerMethodFactory;
	}

	/**
	 * Return the {@link MessageHandlerMethodFactory}.
	 * @return the messageHandlerMethodFactory
	 */
	protected MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
		return this.messageHandlerMethodFactory;
	}

	@Override
	protected MessagingMessageListenerAdapter createMessageListener(NatsListenerContainer container, MessageConverter messageConverter) {
		Assert.state(this.messageHandlerMethodFactory != null, "Could not create message listener - MessageHandlerMethodFactory not set");
		MessagingMessageListenerAdapter messageListener = createMessageListenerInstance(messageConverter);
		messageListener.setHandlerMethod(configureListenerAdapter(messageListener));
		return messageListener;
	}

	/**
	 * Create a {@link HandlerAdapter} for this listener adapter.
	 * @param messageListener the listener adapter.
	 * @return the handler adapter.
	 */
	protected HandlerAdapter configureListenerAdapter(MessagingMessageListenerAdapter messageListener) {
		InvocableHandlerMethod invocableHandlerMethod = this.messageHandlerMethodFactory.createInvocableHandlerMethod(getBean(), getMethod());
		return new HandlerAdapter(invocableHandlerMethod);
	}

	/**
	 * Create an empty {@link MessagingMessageListenerAdapter} instance.
	 * @param messageConverter the converter (may be null).
	 * @return the {@link MessagingMessageListenerAdapter} instance.
	 */
	protected MessagingMessageListenerAdapter createMessageListenerInstance(MessageConverter messageConverter) {
		MessagingMessageListenerAdapter messageListener = new MessagingMessageListenerAdapter(this.bean, this.method);
		if (messageConverter instanceof MessagingMessageConverter) {
			messageListener.setMessageConverter(messageConverter);
		}
		return messageListener;
	}

	@Override
	protected StringBuilder getEndpointDescription() {
		return super.getEndpointDescription()
				.append(" | bean='").append(this.bean).append("'")
				.append(" | method='").append(this.method).append("'");
	}

}