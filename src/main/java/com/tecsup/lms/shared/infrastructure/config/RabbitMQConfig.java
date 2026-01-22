package com.tecsup.lms.shared.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * - Exchange            -->  "lms.event"
 * - Course Queue        -->  "lms.course"
 * - Routing Key for Create Course -->  "course.created"
 */
@Configuration
public class RabbitMQConfig {

    // Exchange Name
    public static final String EXCHANGE_NAME = "lms.event";
    public static final String EXCHANGE_DLQ_NAME = "lms.event.dlq";

    // Queues
    public static final String COURSE_QUEUE = "lms.queue.course";
    public static final String PAYMENT_QUEUE = "lms.queue.payment";

    // Queues DLQ
    public static final String PAYMENT_DLQ = "lms.queue.payment.dlq";

    // Routing Keys
    public static final String COURSE_CREATED_RK = "course.created";
    public static final String COURSE_PUBLISHED_RK = "course.published";


    // -- Exchanges

    /**
     * Event Exchange
     * @return
     */
    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // -- Exchanges DLQ

    @Bean
    public DirectExchange eventDLQExchange() {
        return new DirectExchange(EXCHANGE_DLQ_NAME);
    }


    // -- Queues

    /**
     * Course Queue
     * @return
     */
    @Bean
    public Queue courseQueue() {
        return new Queue(COURSE_QUEUE, true);
    }


    /**
     *  Payment Queue
     * @return
     */
    @Bean
    public Queue paymentQueue() {
        //*
        Map<String, Object> args = Map.of(
                "x-dead-letter-exchange", EXCHANGE_DLQ_NAME,
                "x-dead-letter-routing-key", PAYMENT_DLQ
        );

        return new Queue(PAYMENT_QUEUE,
                true,false, false, args);
        // */
        // return new Queue(PAYMENT_QUEUE, true);
    }

    // -- Queues DLQ

    @Bean
    public Queue paymentDLQ() {
        return new Queue(PAYMENT_DLQ, true);
    }



    // -- Bindings

    /**
     * Course Queue Binding to Event Exchange with Course Created Routing Key
     */
    @Bean
    public Binding courseBinding() {
        // Binding code would go here
        return BindingBuilder
                .bind(courseQueue())
                .to(eventExchange())
                .with(COURSE_CREATED_RK);
    }

    @Bean
    public Binding paymentBinding() {
        // Binding code would go here
        return BindingBuilder
                .bind(paymentQueue())
                .to(eventExchange())
                .with(COURSE_PUBLISHED_RK);
    }


    // -- Bindings  DLQ

    @Bean
    public Binding paymentDLQBinding() {
        return BindingBuilder
                .bind(paymentDLQ())  // Queue DLQ
                .to(eventDLQExchange()) // Exchange DLQ
                .with(PAYMENT_DLQ); // Routing Key DLQ
    }

    /**
     * Bean for serializacion
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
