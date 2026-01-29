# IMPLEMENTACIÓN DEL PATRON SAGA 

Contexto :
Se desea realizar la matricula de un estudiante en un curso,a continuacion se describen los pasos a seguir para llevar a cabo este proceso utilizando el patrón Saga para manejar las transacciones distribuidas y asegurar la consistencia de los datos.
- 1.- EL estudiante solicita la matricula en un curso. (Enrollment)
- 2.- El sistema verifica si el estudiante realizo el pago de la matricula. (Payment)
- 3.- Si el pago es exitoso, se procede a registrar al estudiante en el curso. ( Enrollment)
- 4.- Si el pago no se realizo correctamente, se cancela la solicitud de matricula. ( Enrollment)

<img src="images/saga_kafka.png" alt="Diagrama del Patrón Saga" />

<img src="images/saga_structure.png"  />

## 1.- Definir el evento EnrollmentRequestedEvent.java

```java

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnrollmentRequest {
    private  String studentId;
    private  String studentName;
    private  String courseId;

    // Nuevo campo
    private BigDecimal amount;

}

```

## 2.- Definir EnrollmentSagaHandler.java

```java
import com.tecsup.lms.enrollments.domain.event.EnrollmentRequestedEvent;
import com.tecsup.lms.shared.infrastructure.event.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}

```

## 3.- Adaptar el controlador EnrollmentController.java , EnrollmentRequest y EnrollmentResponse

EnrollmentRequest.java
```java

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnrollmentRequest {
    private  String studentId;
    private  String studentName;
    private  String courseId;

    // Nuevo campo
    private BigDecimal amount;

}

```

EnrollmentResponse.java
```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class EnrollmentResponse {
    private String enrollmentId;

    // nuevos campos
    private String status;
    private String message;
}

```
EnrollmentController.java

- Agregar el siguiente método al controlador:

```java

    @PostMapping
    public ResponseEntity<EnrollmentResponse>
    enrollStudent(@RequestBody EnrollmentRequest request) {
    
        EnrollStudentCommand command = new EnrollStudentCommand(
                request.getStudentId(),
                request.getStudentName(),
                request.getCourseId()
        );
    
    
        String enrollmentId = enrollmentCommandHandler.enrollStudent(command);
    
        // ADAPTAR --------------------------------
        return ResponseEntity.ok(EnrollmentResponse
                .builder()
                .enrollmentId(enrollmentId)
                .build());
        // ADAPTAR --------------------------------
    }


    // ========================================
    // SAGA
    // ========================================

    private final EnrollmentSagaHandler sagaHandler;

    @PostMapping("/request")
    public ResponseEntity<EnrollmentResponse> requestEnrollment(
            @RequestBody EnrollmentRequest request) {

        // Iniciar saga
        String enrollmentId = this.sagaHandler.requestEnrollment(request.getStudentId(),
                request.getStudentName(),
                request.getCourseId(),
                request.getAmount());

        EnrollmentResponse response = EnrollmentResponse.builder()
                                    .enrollmentId(enrollmentId)
                                    .status("PENDING")
                                    .message("Enrollment request is being processed")
                                    .build();

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }
```

## 4.- Adaptar el nuevo topic "enrollment-requests" en KafkaConfig.java

Hacer las siguientes modificaciones en KafkaConfig.java:
```.java

    // SAGA
    public static final String ENROLLMENT_REQUEST_TOPIC = "enrollment.requested";

    // SAGA
    @Bean
    public NewTopic enrollmentRequestedTopic() {
        return TopicBuilder
                .name(ENROLLMENT_REQUEST_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

```
## 5.- Adaptar en KafkaPublisher.java para enviar el evento EnrollmentRequestedEvent

Adaptar el método publish para manejar el nuevo evento EnrollmentRequestedEvent
``` .java

        private String getTopicFromEvent(DomainEvent event) {

        if ( event instanceof CourseCreatedEvent ||
                event instanceof CoursePublishedEvent) {
            return KafkaConfig.COURSE_EVENTS_TOPIC;
        } else if (event instanceof EnrollmentRequestedEvent) {  // AGREGAR
            return KafkaConfig.ENROLLMENT_REQUEST_TOPIC;         // AGREGAR
        } else {
            throw new IllegalArgumentException("Unknown event type: " + event.getEventType());
        }

    }

```
## 6.- Pruebas

```json
GET http://localhost:8080/api/enrollments/request
{
"studentId": "student-21",
"studentName": "Jose Leon",
"courseId": "course-100",
"amount": 99.99
} 
```



