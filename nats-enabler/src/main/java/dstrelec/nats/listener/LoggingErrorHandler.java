/*
 * Copyright 2015-2017 the original author or authors.
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

package dstrelec.nats.listener;

import io.nats.client.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.ObjectUtils;

/**
 * The {@link ErrorHandler} implementation for logging purpose.
 *
 * @author Marius Bogoevici
 * @author Gary Russell
 * @author Dario Strelec
 *
 */
public class LoggingErrorHandler implements ErrorHandler {

	private static final Log log = LogFactory.getLog(LoggingErrorHandler.class);

	@Override
	public void handle(Exception thrownException, Message message) {
		log.error("Error while processing: " + ObjectUtils.nullSafeToString(message), thrownException);
	}

}