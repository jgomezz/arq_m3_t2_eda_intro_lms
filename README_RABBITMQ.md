# MIGRACIÓN A RABBITMQ

- Workflow
  <img src="images/rabbitmq_workflow.png" alt="RabbitMQ" />

- Clases a modificar
<img src="images/rabbitmq_class.png" alt="RabbitMQ" />


## **I.- Creación del servidor de RabbitMQ**

1. Crear el docker compose para RabbitMQ : docker-compose.yml

docker-compose.yml

```yaml
services:
  rabbitmq:
    image: rabbitmq:3-management
    container_name: lms-rabbitmq
    ports:
      - "5672:5672"       # Puerto para conexiones AMQP
      - "15672:15672"     # Puerto para la interfaz de administración
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
volumes:
  rabbitmq_data:
```
- Ejecutar el docker compose en la carpeta donde se encuentra el archivo docker-compose.yml

```bash

docker-compose up -d

```
- Acceder al enlace http://localhost:15672
  - Usuario: admin
  - Clave: admin123

## **II.- Configuración de RabbitMQ en la aplicación Spring Boot**

2.- Agregar dependencias de RabbitMQ en el archivo pom.xml

```xml
        <!-- Spring AMQP / RabbitMQ -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.amqp</groupId>
            <artifactId>spring-rabbit-test</artifactId>
            <scope>test</scope>
        </dependency>

```

3.- Configurar la conexión a RabbitMQ en application.properties

```properties

# Configuration for RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=admin123

```

4.- Crear la configuración de RabbitMQ : RabbitMQConfig.java

- Exchange            -->  "lms.event"
- Course Queue        -->  "lms.course"
- Event Create Course -->  "course.created"

```java    
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * - Exchange            -->  "lms.event"
 * - Course Queue        -->  "lms.course"
 * - Routing Key for Create Course -->  "course.created"
 */
@Configuration
public class RabbitMQConfig {

    // Exchange Name
    public static final String EXCHANGE_NAME = "lms.event";

    // Queues
    public static final String COURSE_QUEUE = "lms.queue.course";

    // Routing Keys
    public static final String COURSE_CREATED_RK = "course.created";


    // -- Exchanges

    /**
     * Event Exchange
     * @return
     */
    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(EXCHANGE_NAME);
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

    /**
     * Bean for serializacion
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}

```

## **III.- Configuración del publicador de eventos en RabbitMQ**

5. Crear el publicador de RabbitMQ : RabbitMQEventPublisher.java

```java
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.tecsup.lms.shared.infrastructure.config.RabbitMQConfig.EXCHANGE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher {

    private final RabbitTemplate template;

    public void publish(String routingKey, DomainEvent event) {

        log.info("Publicando en RabbitMQ: {} [{}]", event.getEventType(), event.getEventId());

        template.convertAndSend(EXCHANGE_NAME
                , routingKey
                , event);

    }
}
```

6. Modificar las siguiente clases

CreateCourseUseCase.java

```java

import com.tecsup.lms.courses.domain.event.CourseCreatedEvent;
import com.tecsup.lms.courses.domain.model.Course;
import com.tecsup.lms.courses.domain.repository.CourseRepository;
import com.tecsup.lms.shared.domain.event.RabbitMQEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.tecsup.lms.shared.infrastructure.config.RabbitMQConfig.COURSE_CREATED_RK;

@Slf4j
@RequiredArgsConstructor
public class CreateCourseUseCase {

    private final CourseRepository repository;

    // private final EventPublisher eventPublisher;
    private final RabbitMQEventPublisher eventPublisher;  // Nueva linea

    public Course createCourse(String title, String description, String instructor) {

        Course course = Course.builder()
                .title(title)
                .description(description)
                .instructor(instructor)
                .status(Course.CourseStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .build();

        Course saved = repository.save(course);
        log.info("Course created: {}", saved.getId());

        // Publicar el evento
        eventPublisher.publish(
                COURSE_CREATED_RK,   //  Nuevo parametro
                new CourseCreatedEvent(
                        saved.getId().toString(),
                        saved.getTitle(),
                        saved.getInstructor()
                )
        );

        return saved;
    }

}

```


BeanConfiguration.java 

```java

import com.tecsup.lms.courses.application.CreateCourseUseCase;
import com.tecsup.lms.courses.application.PublishCourseUseCase;
import com.tecsup.lms.courses.domain.repository.CourseRepository;
import com.tecsup.lms.shared.domain.event.EventPublisher;
import com.tecsup.lms.shared.domain.event.RabbitMQEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CONFIGURACIÓN DE BEANS
 * 
 * Registra los Use Cases y Domain Services como beans de Spring.
 * 
 * Nota: Lombok @RequiredArgsConstructor se encarga de la inyección,
 * aquí solo creamos las instancias.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public CreateCourseUseCase createCourseUseCase(CourseRepository repository, RabbitMQEventPublisher eventPublisher) {
        return new CreateCourseUseCase(repository, eventPublisher);
    }

    @Bean
    public PublishCourseUseCase publishCourseUseCase(CourseRepository repository, EventPublisher eventPublisher) {
        return new PublishCourseUseCase(repository, eventPublisher);
    }
}

```

7.- Realizar la creación de un curso y revisar en la consola del RabbitMQ que se ha recibido el mensaje

## **IV.- Configuración del consumidor de eventos en RabbitMQ**

8.- Crear el consumidor de RabbitMQ : CourseEventHandler.java


<img src="images/rabbitmq_consumer_class.png" alt="RabbitMQ" />

```java


import com.tecsup.lms.courses.domain.event.CourseCreatedEvent;
import com.tecsup.lms.shared.infrastructure.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CourseEventHandler {

    //@EventListener                                
    @RabbitListener(queues = RabbitMQConfig.COURSE_QUEUE )  // Agregar
    public void handleCourseCreated(CourseCreatedEvent event) {

        log.info(" [RabbitMQ] Manejando evento de curso creado: {} - {} - {}",
                event.getCourseId(),
                event.getTitle(),
                event.getInstructor()
        );
        // Aquí se podría agregar la lógica para enviar notificaciones, por ejemplo.

        sendEmailNotification(event);
    }

    private void sendEmailNotification(CourseCreatedEvent event) {
        // Lógica simulada para enviar un correo electrónico
        log.info(" [RabbitMQ] Enviando notificación por correo electrónico para el curso creado: {} - {}",
                event.getCourseId(),
                event.getTitle()
        );
    }
    
}

```

## EJERCICIO DE MIGRACIÓN A RABBITMQ

- Migrar a RabbitMQ el evento de Publicación de Cursos.

## EJERCICIO DE CONSUMIDOR 

- Crear un consumidor de RabbitMQ que escuche las Publicaciones de cursos.


## **V.- Configuración DLQ en RabbitMQ**

9.- Definir los intentos máximos de reintento y el tiempo de espera entre reintentos en application.properties

```properties

# Retry
spring.rabbitmq.listener.simple.retry.enabled=true
spring.rabbitmq.listener.simple.retry.max-attempts=3
spring.rabbitmq.listener.simple.retry.initial-interval=1000
spring.rabbitmq.listener.simple.retry.multiplier=2.0

# DLQ
spring.rabbitmq.listener.simple.default-requeue-rejected=false

```

10.- Modificar la configuración de RabbitMQ para agregar las colas DLQ : RabbitMQConfig.java

-> EXCHANGE_DLQ_NAME --------- (PAYMENT_DLQ) ---------> PAYMENT_DLQ

```java

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
    public static final String EXCHANGE_DLQ_NAME = "lms.event.dlq";  // AGREGAR

    // Queues
    public static final String COURSE_QUEUE = "lms.queue.course";
    public static final String PAYMENT_QUEUE = "lms.queue.payment";

    // Queues DLQ
    public static final String PAYMENT_DLQ = "lms.queue.payment.dlq"; // AGREGAR

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

    /**
     * Event DLQ Exchange  AGREGAR
     * @return
     */
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
     * 
     * @return
     */
    @Bean
    public Queue paymentQueue() {   // MODIFICAR
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

    /**
     *  Payment DLQ Queue   // AGREGAR
     * 
     * @return
     */
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

    /**
     *  Payment DLQ Binding   // AGREGAR
     * @return
     */
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

```

11.- Modificar el consumidor de DLQ : PaymentDLQEventHandler.java

```java


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

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_QUEUE )   // REEMPLAZA LAS ANOTACIONES ANTERIORES
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

```