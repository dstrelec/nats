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
import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import dstrelec.nats.listener.adapter.DelegatingInvocableHandler;
import dstrelec.nats.listener.adapter.HandlerAdapter;
import dstrelec.nats.listener.adapter.MessagingMessageListenerAdapter;

/**
 * The {@link MethodNatsListenerEndpoint} extension for several POJO methods
 * based on the {@link dstrelec.nats.annotation.NatsHandler}.
 *
 * @author Gary Russell
 * @author Dario Strelec
 *
 * @see dstrelec.nats.annotation.NatsHandler
 * @see DelegatingInvocableHandler
 */
public class MultiMethodNatsListenerEndpoint extends MethodNatsListenerEndpoint {

	private final List<Method> methods;

	public MultiMethodNatsListenerEndpoint(List<Method> methods, Object bean) {
		this.methods = methods;
		setBean(bean);
	}

	@Override
	protected HandlerAdapter configureListenerAdapter(MessagingMessageListenerAdapter messageListener) {
		List<InvocableHandlerMethod> invocableHandlerMethods = new ArrayList<>();
		for (Method method : this.methods) {
			invocableHandlerMethods.add(getMessageHandlerMethodFactory().createInvocableHandlerMethod(getBean(), method));
		}
		DelegatingInvocableHandler delegatingHandler = new DelegatingInvocableHandler(invocableHandlerMethods, getBean());
		return new HandlerAdapter(delegatingHandler);
	}

}