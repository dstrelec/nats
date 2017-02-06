Nats Enabler
==================

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This project provides support for using Spring and Java with [NATS messaging system](https://nats.io/).
 
It provides a "template" as a high-level abstraction for sending messages. 
It also provides support for Message-driven POJOs with `@NatsListener` annotations and a "listener container". 
These libraries promote the use of dependency injection and declarative.
 
The project was inspired by  [Spring Kafka](http://projects.spring.io/spring-kafka/) and 
[Spring AMQP](http://projects.spring.io/spring-amqp/) projects, so you will see a lots of similarities
to those Spring messaging projects.

# Features
* Listener container for asynchronous processing of inbound messages
* NatsTemplate for sending messages

# Quick Start
The recommended way to get started using `nats-enabler` in your project is with a dependency management 
system – the snippet below can be copied and pasted into your build. 

    <dependencies>
        <dependency>
            <groupId>dstrelec.nats</groupId>
            <artifactId>nats-enabler</artifactId>
            <version>0.1.0</version>
        </dependency>
    </dependencies>

## Checking out and Building
To check out the project and build from source, do the following:

    git clone git://github.com/dstrelec/nats.git
    cd nats
    maven install

The Java SE 7 or higher is recommended to build the project.

## Basic Usage

```java
@EnableNats
@Configuration
public class AppConfig {
	
    @Bean
    public NatsConnectionFactory natsConnectionFactory() {
        return new DefaultConnectionFactory();
    }
    
    @Bean
    public NatsTemplate natsTemplate() {
        return new NatsTemplate(natsConnectionFactory());
    }
    
    @Bean
    public NatsListenerContainerFactory natsListenerContainerFactory() {
        DefaultNatsListenerContainerFactory factory = new DefaultNatsListenerContainerFactory();
        factory.setConnectionFactory(natsConnectionFactory());
        factory.setMessageConverter(new StringJsonMessageConverter());
        return factory;
    }
}
```
```java
@Component
public class MyComponent {
	
    @Autowired
    private NatsTemplate natsTemplate;
	
    @PostConstruct
    public void init() {
        natsTemplate.publish("foo", "\"Hello world.\"");
    }
    
}
```

# Resources

For more information, please visit the Nats Enabler website at:
[Reference Manual](http://dstrelec.github.io/nats/docs)


# License

Nats Enabler is released under the terms of the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
