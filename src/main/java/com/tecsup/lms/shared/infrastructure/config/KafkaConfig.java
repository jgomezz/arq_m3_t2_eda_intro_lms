package com.tecsup.lms.shared.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

/**
 * KafkaConfig
 *
 *             Topic       -->     Particiones
 *       Eventos del curso             3
 *          course.events
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    // Setting topics
    public static final String COURSE_EVENTS_TOPIC = "course.events";

    // DLQ
    public static final String DLQ_COURSE_EVENTS_TOPIC = "dlq.course.events";  // ✅ DLQ Topic

    // SAGA
    public static final String ENROLLMENT_REQUEST_TOPIC = "enrollment.requested";


    // Setting Queues/Partitions

    /**
     *  Topic de eventos de cursos
     * @return
     */
    @Bean
    public NewTopic courseEventsTopic() {
        return new NewTopic(COURSE_EVENTS_TOPIC, // topic
                3,  // Nro particiones
                (short) 1  // Nro de replicas
        );
    }

    // DLQ
    @Bean
    public NewTopic dlqCourseEventsTopic() {
        return TopicBuilder.name(DLQ_COURSE_EVENTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    // SAGA
    @Bean
    public NewTopic enrollmentRequestedTopic() {
        return TopicBuilder
                .name(ENROLLMENT_REQUEST_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

}
