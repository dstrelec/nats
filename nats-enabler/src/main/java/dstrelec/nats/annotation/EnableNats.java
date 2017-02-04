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

package dstrelec.nats.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dstrelec.nats.config.DefaultNatsListenerContainerFactory;
import dstrelec.nats.listener.NatsListenerContainer;
import org.springframework.context.annotation.Import;

/**
 * Enable Nats listener annotated endpoints that are created under the covers by a
 * {@link DefaultNatsListenerContainerFactory
 * DefaulttListenerContainerFactory}. To be used on
 * {@link org.springframework.context.annotation.Configuration Configuration} classes as
 * follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableNats
 * public class AppConfig {
 * 	&#064;Bean
 * 	public DefaultNatsListenerContainerFactory myNatsListenerContainerFactory() {
 * 		DefaultNatsListenerContainerFactory factory = new DefaultNatsListenerContainerFactory();
 * 		factory.setConnectionFactory(connectionFactory());
 * 		return factory;
 * 	}
 * 	// other &#064;Bean definitions
 * }
 * </pre>
 *
 * The {@code NatsListenerContainerFactory} is responsible to create the listener
 * container for a particular endpoint. Typical implementations, as the
 * {@link DefaultNatsListenerContainerFactory
 * DefaultNatsListenerContainerFactory} used in the sample above, provides the necessary
 * configuration options that are supported by the underlying
 * {@link NatsListenerContainer
 * NatsListenerContainer}.
 *
 * <p>
 * {@code @EnableNats} enables detection of {@link NatsListener} annotations on any
 * Spring-managed bean in the container. For example, given a class {@code MyService}:
 *
 * <pre class="code">
 * package com.acme.foo;
 *
 * public class MyService {
 * 	&#064;NatsListener(containerFactory = "myNatsListenerContainerFactory", subject = "mySubject")
 * 	public void process(String msg) {
 * 		// process incoming message
 * 	}
 * }
 * </pre>
 *
 * The container factory to use is identified by the
 * {@link NatsListener#containerFactory() containerFactory} attribute defining the name
 * of the {@code NatsListenerContainerFactory} bean to use. When none is set a
 * {@code NatsListenerContainerFactory} bean with name
 * {@code NatsListenerContainerFactory} is assumed to be present.
 *
 * <p>
 * the following configuration would ensure that every time a message is receied from
 * subject "mySubject", {@code MyService.process()} is called with the content of the message:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableNats
 * public class AppConfig {
 * 	&#064;Bean
 * 	public MyService myService() {
 * 		return new MyService();
 * 	}
 *
 * 	// Nats infrastructure setup
 * }
 * </pre>
 *
 * Alternatively, if {@code MyService} were annotated with {@code @Component}, the
 * following configuration would ensure that its {@code @NatsListener} annotated method
 * is invoked with a matching incoming message:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableNats
 * &#064;ComponentScan(basePackages = "com.acme.foo")
 * public class AppConfig {
 * }
 * </pre>
 *
 * Note that the created containers are not registered with the application context but
 * can be easily located for management purposes using the
 * {@link dstrelec.nats.config.NatsListenerEndpointRegistry
 * NatsListenerEndpointRegistry}.
 *
 * <p>
 * Annotated methods can use a flexible signature; in particular, it is possible to use
 * the {@link org.springframework.messaging.Message Message} abstraction and related
 * annotations, see {@link NatsListener} Javadoc for more details.
 *
 * These features are abstracted by the
 * {@link org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory
 * MessageHandlerMethodFactory} that is responsible to build the necessary invoker to
 * process the annotated method. By default,
 * {@link org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory
 * DefaultMessageHandlerMethodFactory} is used.
 *
 * <p>
 * When more control is desired, a {@code @Configuration} class may implement
 * {@link NatsListenerConfigurer}. This allows access to the underlying
 * {@link dstrelec.nats.config.NatsListenerEndpointRegistrar
 * NatsListenerEndpointRegistrar} instance. The following example demonstrates how to
 * specify an explicit default {@code NatsListenerContainerFactory}
 *
 * <pre class="code">
 * {
 * 	&#64;code
 * 	&#064;Configuration
 * 	&#064;EnableNats
 * 	public class AppConfig implements NatsListenerConfigurer {
 * 		&#064;Override
 * 		public void configureNatsListeners(NatsListenerEndpointRegistrar registrar) {
 * 			registrar.setContainerFactory(myNatsListenerContainerFactory());
 * 		}
 *
 * 		&#064;Bean
 * 		public NatsListenerContainerFactory&lt;?, ?&gt; myNatsListenerContainerFactory() {
 * 			// factory settings
 * 		}
 *
 * 		&#064;Bean
 * 		public MyService myService() {
 * 			return new MyService();
 * 		}
 * 	}
 * }
 * </pre>
 *
 * It is also possible to specify a custom
 * {@link dstrelec.nats.config.NatsListenerEndpointRegistry
 * NatsListenerEndpointRegistry} in case you need more control on the way the containers
 * are created and managed. The example below also demonstrates how to customize the
 * {@code NatsHandlerMethodFactory} to use with a custom
 * {@link org.springframework.validation.Validator Validator} so that payloads annotated
 * with {@link org.springframework.validation.annotation.Validated Validated} are first
 * validated against a custom {@code Validator}.
 *
 * <pre class="code">
 * {
 * 	&#64;code
 * 	&#064;Configuration
 * 	&#064;EnableNats
 * 	public class AppConfig implements NatsListenerConfigurer {
 * 		&#064;Override
 * 		public void configureNatsListeners(NatsListenerEndpointRegistrar registrar) {
 * 			registrar.setEndpointRegistry(myNatsListenerEndpointRegistry());
 * 			registrar.setMessageHandlerMethodFactory(myMessageHandlerMethodFactory);
 * 		}
 *
 * 		&#064;Bean
 * 		public NatsListenerEndpointRegistry myNatsListenerEndpointRegistry() {
 * 			// registry configuration
 * 		}
 *
 * 		&#064;Bean
 * 		public NatsHandlerMethodFactory myMessageHandlerMethodFactory() {
 * 			DefaultNatsHandlerMethodFactory factory = new DefaultNatsHandlerMethodFactory();
 * 			factory.setValidator(new MyValidator());
 * 			return factory;
 * 		}
 *
 * 		&#064;Bean
 * 		public MyService myService() {
 * 			return new MyService();
 * 		}
 * 	}
 * }
 * </pre>
 *
 * Implementing {@code NatsListenerConfigurer} also allows for fine-grained control over
 * endpoints registration via the {@code NatsListenerEndpointRegistrar}. For example, the
 * following configures an extra endpoint:
 *
 * <pre class="code">
 * {
 * 	&#64;code
 * 	&#064;Configuration
 * 	&#064;EnableNats
 * 	public class AppConfig implements NatsListenerConfigurer {
 * 		&#064;Override
 * 		public void configureNatsListeners(NatsListenerEndpointRegistrar registrar) {
 * 			SimpleNatsListenerEndpoint myEndpoint = new SimpleNatsListenerEndpoint();
 * 			// ... configure the endpoint
 * 			registrar.registerEndpoint(endpoint, anotherNatsListenerContainerFactory());
 * 		}
 *
 * 		&#064;Bean
 * 		public MyService myService() {
 * 			return new MyService();
 * 		}
 *
 * 		&#064;Bean
 * 		public NatsListenerContainerFactory&lt;?, ?&gt; anotherNatsListenerContainerFactory() {
 * 			// ...
 * 		}
 *
 * 		// Nats infrastructure setup
 * 	}
 * }
 * </pre>
 *
 * Note that all beans implementing {@code NatsListenerConfigurer} will be detected and
 * invoked in a similar fashion. The example above can be translated in a regular bean
 * definition registered in the context in case you use the XML configuration.
 *
 * @author Stephane Nicoll
 * @author Gary Russell
 * @author Dario Strelec
 *
 * @see NatsListener
 * @see NatsListenerAnnotationBeanPostProcessor
 * @see dstrelec.nats.config.NatsListenerEndpointRegistrar
 * @see dstrelec.nats.config.NatsListenerEndpointRegistry
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NatsBootstrapConfiguration.class)
public @interface EnableNats {
}