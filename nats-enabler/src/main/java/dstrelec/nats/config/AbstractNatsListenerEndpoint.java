/*
 * Copyright 2014-2017 the original author or authors.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import dstrelec.nats.listener.MessageListener;
import dstrelec.nats.listener.NatsListenerContainer;
import dstrelec.nats.listener.adapter.FilteringMessageListenerAdapter;
import dstrelec.nats.listener.adapter.MessageFilterStrategy;
import dstrelec.nats.listener.adapter.MessagingMessageListenerAdapter;
import dstrelec.nats.support.converter.MessageConverter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;

/**
 * Base model for a Nats listener endpoint.
 *
 * @author Stephane Nicoll
 * @author Gary Russell
 * @author Dario Strelec
 *
 * @see MethodNatsListenerEndpoint
 */
public abstract class AbstractNatsListenerEndpoint implements NatsListenerEndpoint, BeanFactoryAware, InitializingBean {

	private String id;

	private final Collection<String> subjects = new ArrayList<>();

	private BeanFactory beanFactory;

	private BeanExpressionResolver resolver;

	private BeanExpressionContext expressionContext;

	private String group;

	private MessageFilterStrategy messageFilterStrategy;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
		if (beanFactory instanceof ConfigurableListableBeanFactory) {
			this.resolver = ((ConfigurableListableBeanFactory) beanFactory).getBeanExpressionResolver();
			this.expressionContext = new BeanExpressionContext((ConfigurableListableBeanFactory) beanFactory, null);
		}
	}

	protected BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	protected BeanExpressionResolver getResolver() {
		return this.resolver;
	}

	protected BeanExpressionContext getBeanExpressionContext() {
		return this.expressionContext;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * Set the subjects to use.
	 * @param subjects to set.
	 */
	public void setSubjects(String... subjects) {
		Assert.notNull(subjects, "'subjects' must not be null");
		this.subjects.clear();
		this.subjects.addAll(Arrays.asList(subjects));
	}

	/**
	 * Return the subjects for this endpoint.
	 * @return the subjects for this endpoint.
	 */
	@Override
	public Collection<String> getSubjects() {
		return Collections.unmodifiableCollection(this.subjects);
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	/**
	 * Set the group for the corresponding listener container.
	 * @param group the group.
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public void afterPropertiesSet() {
		if (getSubjects().isEmpty()) {
			throw new IllegalStateException("Subjects must be provided for " + this);
		}
	}

	protected MessageFilterStrategy getMessageFilterStrategy() {
		return this.messageFilterStrategy;
	}

	/**
	 * Set a {@link MessageFilterStrategy} implementation.
	 * @param messageFilterStrategy the strategy implementation.
	 */
	public void setMessageFilterStrategy(MessageFilterStrategy messageFilterStrategy) {
		this.messageFilterStrategy = messageFilterStrategy;
	}

	@Override
	public void setupListenerContainer(NatsListenerContainer listenerContainer, MessageConverter messageConverter) {
		setupMessageListener(listenerContainer, messageConverter);
	}

	/**
	 * Create a {@link MessageListener} that is able to serve this endpoint for the
	 * specified container.
	 * @param container the {@link NatsListenerContainer} to create a {@link MessageListener}.
	 * @param messageConverter the message converter - may be null.
	 * @return a a {@link MessageListener} instance.
	 */
	protected abstract MessagingMessageListenerAdapter createMessageListener(NatsListenerContainer container,
																				   MessageConverter messageConverter);

	@SuppressWarnings("unchecked")
	private void setupMessageListener(NatsListenerContainer container, MessageConverter messageConverter) {
		MessageListener messageListener = createMessageListener(container, messageConverter);
		Assert.state(messageListener != null, "Endpoint [" + this + "] must provide a non null message listener");
		if (this.messageFilterStrategy != null) {
			messageListener = new FilteringMessageListenerAdapter(messageListener, this.messageFilterStrategy);
		}
		container.setupMessageListener(messageListener);
	}

	/**
	 * Return a description for this endpoint.
	 * @return a description for this endpoint.
	 * <p>Available to subclasses, for inclusion in their {@code toString()} result.
	 */
	protected StringBuilder getEndpointDescription() {
		StringBuilder result = new StringBuilder();
		return result.append(getClass().getSimpleName()).append("[").append(this.id).
				append("] subjects=").append(this.subjects).append("'");
	}

	@Override
	public String toString() {
		return getEndpointDescription().toString();
	}

}