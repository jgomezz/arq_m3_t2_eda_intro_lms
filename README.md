## I - Uso de EDA en Plataforma de Cursos Online


### 1.- Create a New Course

POST : http://localhost:8080/api/courses

```
{
"title": "Spring Boot Masterclass",
"description": "Learn Spring Boot from scratch",
"instructor": "John Doe"
}
```
### 2.- Publish a Course

PUT : http://localhost:8080/api/courses/1/publish?price=10

## II - Ejercicios

### Crear Eventos y Handlers para los siguientes casos de uso:

#### StudentEnrolledEvent
- Handler: Enviar email de bienvenida
- Handler: Actualizar estadísticas del curso
- Handler: Crear acceso al material

#### LessonCompletedEvent
- Handler: Actualizar progreso
- Handler: Enviar notificación de logro
- Handler: Verificar si completó el curso


