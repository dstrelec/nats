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

package dstrelec.nats.support;

/**
 * The Nats specific message headers constants.
 *
 * @author Artem Bilan
 * @author Marius Bogoevici
 * @author Gary Russell
 * @author Dario Strelec
 *
 */
public abstract class NatsHeaders {

	private static final String PREFIX = "nats_";

	/**
	 * The header containing the subject when sending data to Nats.
	 */
	public static final String SUBJECT = PREFIX + "subject";

	/**
	 * The header containing the reply to when sending data to Nats.
	 */
	public static final String REPLY_TO = PREFIX + "reply_to";

}