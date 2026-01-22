package com.tecsup.lms.payment.application.eventhandler;

import com.tecsup.lms.courses.domain.event.CoursePublishedEvent;
import com.tecsup.lms.shared.infrastructure.config.RabbitMQConfig;
import com.tecsup.lms.shared.infrastructure.dlq.DeadLetterQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentHandler {

    private final Random random = new Random();

    private final DeadLetterQueue dlq;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_QUEUE )
    public void handleCoursePublished(CoursePublishedEvent event) throws InterruptedException {
        log.info(" [RabbitMQ] Handling course published event for payment: {} - {} - ${}",
                event.getCourseId(),
                event.getTitle(),
                event.getPrice()
        );

        log.info("[{}] Processing payment ...", Thread.currentThread().getName());

        if (random.nextBoolean()) {
            log.info("Payment processing taking longer than expected...");
            throw new RuntimeException("Payment processing failed due to timeout");
        }

        log.info("Payment finished for course: {}", event.getTitle());

    }

}
