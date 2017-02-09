/*
 * Copyright 2012-2017 the original author or authors.
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

package dstrelec.nats.autoconfigure;

import dstrelec.nats.core.DefaultConnectionFactory;
import dstrelec.nats.core.NatsConnectionFactory;
import dstrelec.nats.core.NatsTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Nats.
 *
 * @author Gary Russell
 * @author Dario Strelec
 */
@Configuration
@ConditionalOnClass(NatsTemplate.class)
@EnableConfigurationProperties(NatsProperties.class)
@Import(NatsAnnotationDrivenConfiguration.class)
public class NatsAutoConfiguration {

	private final NatsProperties properties;

	public NatsAutoConfiguration(NatsProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean(NatsTemplate.class)
	public NatsTemplate natsTemplate(NatsConnectionFactory connectionFactory) {
		NatsTemplate natsTemplate = new NatsTemplate(connectionFactory);
		natsTemplate.setDefaultSubject(properties.getTemplate().getDefaultSubject());
		return natsTemplate;
	}

	@Bean
	@ConditionalOnMissingBean(NatsConnectionFactory.class)
	public NatsConnectionFactory natsConnectionFactory() {
		DefaultConnectionFactory connectionFactory = new DefaultConnectionFactory();
		BeanUtils.copyProperties(properties, connectionFactory);
		if (properties.getServers() != null) {
			connectionFactory.setServers(properties.getServers().toArray(new String[]{}));
		}

		return connectionFactory;
	}

}