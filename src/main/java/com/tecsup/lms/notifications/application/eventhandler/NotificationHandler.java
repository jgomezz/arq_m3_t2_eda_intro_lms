package com.tecsup.lms.notifications.application.eventhandler;

import com.tecsup.lms.courses.domain.event.CourseCreatedEvent;
import com.tecsup.lms.courses.domain.event.CoursePublishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationHandler {

    @Async("eventExecutor") // No generara bloqueos
    @EventListener
    public void handleCoursePublished(CoursePublishedEvent event) throws InterruptedException {
        log.info("[{}] Sending notifications...", Thread.currentThread().getName());
        Thread.sleep(4000);
        log.info("📧 Email sent for course: {}", event.getTitle());

    }
}
