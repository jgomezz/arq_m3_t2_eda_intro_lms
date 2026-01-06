package com.tecsup.lms.courses.domain.repository;

import com.tecsup.lms.courses.domain.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}