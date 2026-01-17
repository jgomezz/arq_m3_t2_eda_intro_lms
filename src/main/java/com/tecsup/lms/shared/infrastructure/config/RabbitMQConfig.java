package com.tecsup.lms.shared.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * - Exchange            -->  "lms.event"
 * - Course Queue        -->  "lms.course"
 * - Event Create Course -->  "course.created"
 */
@Configuration
public class RabbitMQConfig {

    // Exchange Name
    public static final String EXCHANGE_NAME = "lms.event";

    // Queues
    public static final String COURSE_QUEUE = "lms.queue.course";

    // Routing Keys
    public static final String COURSE_CREATED_RK = "course.created";


    // -- Exchanges

    /**
     * Event Exchange
     * @return
     */
    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // -- Queues

    /**
     * Course Queue
     * @return
     */
    public Queue courseQueue() {
        return new Queue(COURSE_QUEUE, true);
    }

    // -- Bindings

    /**
     * Course Queue Binding to Event Exchange with Course Created Routing Key
     */
    public Binding courseBinding() {
        // Binding code would go here
        return BindingBuilder
                .bind(courseQueue())
                .to(eventExchange())
                .with(COURSE_CREATED_RK);
    }

}
