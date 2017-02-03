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

package dstrelec.nats.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dstrelec.nats.config.NatsListenerContainerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;

/**
 * Annotation that marks a method to be the target of a Nats message listener on the
 * specified subject.
 *
 * The {@link #containerFactory()} identifies the
 * {@link NatsListenerContainerFactory
 * NatsListenerContainerFactory} to use to build the Nats listener container. If not
 * set, a <em>default</em> container factory is assumed to be available with a bean name
 * of {@code natsListenerContainerFactory} unless an explicit default has been provided
 * through configuration.
 *
 * <p>
 * Processing of {@code @NatsListener} annotations is performed by registering a
 * {@link NatsListenerAnnotationBeanPostProcessor}. This can be done manually or, more
 * conveniently, through {@link EnableNats} annotation.
 *
 * <p>
 * Annotated methods are allowed to have flexible signatures similar to what
 * {@link MessageMapping} provides, that is
 * <ul>
 * <li>{@link io.nats.client.Message} to access to the raw Nats message</li>
 * <li>{@link org.springframework.messaging.handler.annotation.Payload @Payload}-annotated
 * method arguments including the support of validation</li>
 * <li>{@link org.springframework.messaging.handler.annotation.Header @Header}-annotated
 * method arguments to extract a specific header value, defined by
 * {@link dstrelec.nats.support.NatsHeaders NatsHeaders}</li>
 * <li>{@link org.springframework.messaging.handler.annotation.Headers @Headers}-annotated
 * argument that must also be assignable to {@link java.util.Map} for getting access to
 * all headers.</li>
 * <li>{@link org.springframework.messaging.MessageHeaders MessageHeaders} arguments for
 * getting access to all headers.</li>
 * <li>{@link org.springframework.messaging.support.MessageHeaderAccessor
 * MessageHeaderAccessor} for convenient access to all method arguments.</li>
 * </ul>
 *
 * <p>When defined at the method level, a listener container is created for each method.
 * The {@link dstrelec.nats.listener.MessageListener} is a
 * {@link dstrelec.nats.listener.adapter.MessagingMessageListenerAdapter},
 * configured with a {@link dstrelec.nats.config.MethodNatsListenerEndpoint}.
 *
 * <p>When defined at the class level, a single message listener container is used to
 * service all methods annotated with {@code @NatsHandler}. Method signatures of such
 * annotated methods must not cause any ambiguity such that a single method can be
 * resolved for a particular inbound message. The
 * {@link dstrelec.nats.listener.adapter.MessagingMessageListenerAdapter} is
 * configured with a
 * {@link dstrelec.nats.config.MultiMethodNatsListenerEndpoint}.
 *
 * @author Gary Russell
 * @author Dario Strelec
 *
 * @see EnableNats
 * @see NatsListenerAnnotationBeanPostProcessor
 * @see NatsListeners
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@MessageMapping
@Documented
@Repeatable(NatsListeners.class)
public @interface NatsListener {

	/**
	 * The unique identifier of the container managing for this endpoint.
	 * <p>If none is specified an auto-generated one is provided.
	 * @return the {@code id} for the container managing for this endpoint.
	 * @see dstrelec.nats.config.NatsListenerEndpointRegistry#getListenerContainer(String)
	 */
	String id() default "";

	/**
	 * The bean name of the {@link NatsListenerContainerFactory}
	 * to use to create the message listener container responsible to serve this endpoint.
	 * <p>If not specified, the default container factory is used, if any.
	 * @return the container factory bean name.
	 */
	String containerFactory() default "";

	/**
	 * The subjects for this listener.
	 * The entries can be 'subject name', 'property-placeholder keys' or 'expressions'.
	 * Expression must be resolved to the subject name.
	 * @return the subject names or expressions (SpEL) to listen to.
	 */
	String[] subjects() default {};

	/**
	 * If provided, the listener container for this listener will be added to a bean
	 * with this value as its name, of type {@code Collection<NatsListenerContainer>}.
	 * This allows, for example, iteration over the collection to start/stop a subset
	 * of containers.
	 * @return the bean name for the group.
	 */
	String group() default "";

}
