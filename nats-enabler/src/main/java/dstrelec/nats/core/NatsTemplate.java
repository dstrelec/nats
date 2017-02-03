/*
 * Copyright 2015-2016 the original author or authors.
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
import io.nats.client.Connection;

import java.io.IOException;

/**
 * A template for executing high-level operations.
 *
 * @author Dario Strelec
 */
public class NatsTemplate implements NatsOperations {

	private NatsConnectionFactory connectionFactory;

	/**
	 * Create an instance using the supplied connection factory.
	 * @param connectionFactory the connection factory.
	 */
	public NatsTemplate(NatsConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	public void publish(String subject, Object data) {
		try {
			getConnection().publish(subject, data.toString().getBytes());
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
