package com.tecsup.lms.courses.infrastructure.web.controller;

import com.tecsup.lms.courses.application.CreateCourseUseCase;
import com.tecsup.lms.courses.application.PublishCourseUseCase;
import com.tecsup.lms.courses.domain.model.Course;
import com.tecsup.lms.courses.infrastructure.web.dto.CreateCourseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CreateCourseUseCase createCourseUseCase;
    private final PublishCourseUseCase publishCourseUseCase;

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody CreateCourseRequest request) {
        Course course = createCourseUseCase.createCourse(
                request.getTitle(),
                request.getDescription(),
                request.getInstructor()
        );
        return ResponseEntity.ok(course);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<Course> publishCourse(
            @PathVariable Long id,
            @RequestParam double price) {

        Course course = publishCourseUseCase.publishCourse(id, price);
        return ResponseEntity.ok(course);
    }
}
