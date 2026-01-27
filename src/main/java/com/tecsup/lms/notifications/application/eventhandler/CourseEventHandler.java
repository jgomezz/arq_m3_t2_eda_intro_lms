package com.tecsup.lms.notifications.application.eventhandler;

import com.tecsup.lms.courses.domain.event.CourseCreatedEvent;
import com.tecsup.lms.shared.domain.event.DomainEvent;
import com.tecsup.lms.shared.infrastructure.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CourseEventHandler {

    @KafkaListener(
            topics = KafkaConfig.COURSE_EVENTS_TOPIC, // Topico a escuchar
            groupId = "courses-service-group"    // Grupo de consumidores
    )
    public void handleCourseEvents(DomainEvent event) {
        if (event instanceof CourseCreatedEvent) {
            handleCourseCreated((CourseCreatedEvent) event);
        }

    }

    /**
     *  Maneja el evento de curso creado
     * @param event
     */
    public void handleCourseCreated(CourseCreatedEvent event) {
        log.info("Manejando evento de curso creado: {} - {} - {}",
                event.getCourseId(),
                event.getTitle(),
                event.getInstructor()
        );
        // Aquí se podría agregar la lógica para enviar notificaciones, por ejemplo.

        sendEmailNotification(event);
    }



    private void sendEmailNotification(CourseCreatedEvent event) {
        // Lógica simulada para enviar un correo electrónico
        log.info("Enviando notificación por correo electrónico para el curso creado: {} - {}",
                event.getCourseId(),
                event.getTitle()
        );
    }


}
