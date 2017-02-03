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

package dstrelec.nats.listener.config;

import java.util.Arrays;

import dstrelec.nats.listener.MessageListener;

import dstrelec.nats.listener.ErrorHandler;

/**
 * Contains runtime properties for a listener container.
 *
 * @author Gary Russell
 * @author Artem Bilan
 * @author Dario Strelec
 */
public class ContainerProperties {

	private static final int DEFAULT_SHUTDOWN_TIMEOUT = 10000;

	/**
	 * Subject names.
	 */
	private final String[] subjects;

	/**
	 * The message listener.
	 */
	private MessageListener messageListener;

	/**
	 * The error handler to call when the listener throws an exception.
	 */
	private ErrorHandler errorHandler;

	/**
	 * The timeout for shutting down the container. This is the maximum amount of
	 * time that the invocation to {@code #stop(Runnable)} will block for, before
	 * returning.
	 */
	private long shutdownTimeout = DEFAULT_SHUTDOWN_TIMEOUT;


	public ContainerProperties(String... subjects) {
		this.subjects = Arrays.asList(subjects).toArray(new String[subjects.length]);
	}

	public ContainerProperties() {
		this.subjects = null;
	}

	/**
	 * Set the message listener.
	 * @param messageListener the listener.
	 */
	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	/**
	 * Set the error handler to call when the listener throws an exception.
	 * @param errorHandler the error handler.
	 */
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * Set the timeout for shutting down the container. This is the maximum amount of
	 * time that the invocation to {@code #stop(Runnable)} will block for, before
	 * returning.
	 * @param shutdownTimeout the shutdown timeout.
	 */
	public void setShutdownTimeout(long shutdownTimeout) {
		this.shutdownTimeout = shutdownTimeout;
	}

	public String[] getSubjects() {
		return this.subjects;
	}

	public long getShutdownTimeout() {
		return this.shutdownTimeout;
	}

	public MessageListener getMessageListener() {
		return this.messageListener;
	}

	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

}