package com.tecsup.lms.payment.application.saga;

import com.tecsup.lms.enrollments.domain.event.EnrollmentRequestedEvent;
import com.tecsup.lms.payment.domain.event.PaymentProcessedEvent;
import com.tecsup.lms.shared.infrastructure.config.KafkaConfig;
import com.tecsup.lms.shared.infrastructure.event.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSagaHandler {

    private final KafkaEventPublisher kafkaEventPublisher;
    private final Random random = new Random();

    @KafkaListener(
            topics = KafkaConfig.ENROLLMENT_REQUESTED_TOPIC,  //"enrollment.requested",
            groupId = "payment-service-group"
    )
    @Transactional
    public void handleEnrollmentRequested(EnrollmentRequestedEvent event) {

        log.info("💳 [PAYMENT] Procesando pago para enrollment");
        log.info("   Enrollment ID: {}", event.getEnrollmentId());
        log.info("   Student: {}", event.getStudentName());
        log.info("   Amount: ${}", event.getAmount());

        try {
            // Simular tiempo de procesamiento
            Thread.sleep(1000 + random.nextInt(2000)); // 1-3 segundos

            // Simular resultado: 60% éxito, 40% fallo
            boolean paymentSuccess = random.nextInt(100) < 60;

            if(paymentSuccess) {

                // PAGO EXITOSO

                log.info("✅ [PAYMENT] Pago procesado exitosamente para enrollment ID: {}", event.getEnrollmentId());
                // Aquí se podría publicar un evento de pago exitoso si fuera necesario

                // PaymentProcessedEvent

                String transactionId = "tx-" + UUID.randomUUID();

                PaymentProcessedEvent processedEvent = new PaymentProcessedEvent(
                        event.getEnrollmentId(),
                        transactionId,
                        event.getAmount(),
                        LocalDateTime.now());

                kafkaEventPublisher.publish(processedEvent);

                log.info("✅ [PAYMENT] Pago procesado exitosamente");
                log.info("   Transaction ID: {}", transactionId);

            } else {
                log.warn("❌ [PAYMENT] El pago falló para enrollment ID: {}", event.getEnrollmentId());
                // Aquí se podría publicar un evento de pago fallido si fuera necesario
                // PaymentFailedEvent

            }


        } catch (Exception e) {
            log.error("💥 [PAYMENT] Error procesando pago", e);
        }

    }

}
