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

import java.util.Map;

import org.springframework.messaging.MessageHeaders;

/**
 * Overload of message headers configurable for adding id and timestamp headers.
 *
 * @author Marius Bogoevici
 * @author Gary Russell
 * @author Dario Strelec
 *
 */
@SuppressWarnings("serial")
public class NatsMessageHeaders extends MessageHeaders {

	/**
	 * Construct headers with or without id and/or timestamp.
	 * @param generateId true to add an ID header.
	 * @param generateTimestamp true to add a timestamp header.
	 */
	NatsMessageHeaders(boolean generateId, boolean generateTimestamp) {
		super(null, generateId ? null : ID_VALUE_NONE, generateTimestamp ? null : -1L);
	}

	@Override
	public Map<String, Object> getRawHeaders() { //NOSONAR - not useless, widening to public
		return super.getRawHeaders();
	}

}