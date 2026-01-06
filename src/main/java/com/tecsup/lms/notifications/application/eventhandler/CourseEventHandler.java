package com.tecsup.lms.notifications.application.eventhandler;

import com.tecsup.lms.courses.domain.event.CourseCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CourseEventHandler {

    @EventListener
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
