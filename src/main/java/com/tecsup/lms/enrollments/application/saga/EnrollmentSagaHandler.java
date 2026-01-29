package com.tecsup.lms.enrollments.application.saga;

import com.tecsup.lms.enrollments.domain.event.EnrollmentRequestedEvent;
import com.tecsup.lms.payment.domain.event.PaymentProcessedEvent;
import com.tecsup.lms.shared.infrastructure.config.KafkaConfig;
import com.tecsup.lms.shared.infrastructure.event.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentSagaHandler {

    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public String requestEnrollment(String studentId, String studentName, String courseId, BigDecimal amount) {

        log.info("🚀 [SAGA] Iniciando saga de matrícula");

        // 1. Generar ID
        String enrollmentId = "enrollment-" + UUID.randomUUID();

        // 2. Publicar evento para iniciar saga
        EnrollmentRequestedEvent requestEvent = new EnrollmentRequestedEvent(
                enrollmentId,
                studentId,
                studentName,
                courseId,
                amount,
                LocalDateTime.now()
        );
        // Publicar el comando de inscripción
        kafkaEventPublisher.publish(requestEvent);

        return enrollmentId;
    }

    /**
     * PASO 2: Reaccionar a pago exitoso
     */
    @KafkaListener(
            topics = KafkaConfig.PAYMENT_PROCESSED_TOPIC,
            groupId = "enrollment-saga-group"
    )
    @Transactional
    public void handlePaymentProcessed(PaymentProcessedEvent event) {

        log.info("💳 [SAGA] Pago procesado exitosamente");
        log.info("   Enrollment ID: {}", event.getEnrollmentId());
        log.info("   Transaction ID: {}", event.getTransactionId());

        // TODO

        log.info("✅ [SAGA] Enrollment confirmada");
        log.info("📨 [SAGA] Evento EnrollmentConfirmed publicado");
    }


}
