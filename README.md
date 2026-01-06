
### Create a New Course

POST : http://localhost:8080/api/courses

```
{
"title": "Spring Boot Masterclass",
"description": "Learn Spring Boot from scratch",
"instructor": "John Doe"
}
```
### Publish a Course

PUT : http://localhost:8080/api/courses/1/publish?price=10


