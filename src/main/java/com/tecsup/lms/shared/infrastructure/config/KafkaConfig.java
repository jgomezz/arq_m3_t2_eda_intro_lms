package com.tecsup.lms.shared.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

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

}
