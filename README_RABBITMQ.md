# MIGRATION A RABBITMQ


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
- Ejecutar el docker compose
```bash
docker-compose up -d
```
- Acceder al enlace http://localhost:15672
  - Usuario: admin
  - Clave: admin123
  
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