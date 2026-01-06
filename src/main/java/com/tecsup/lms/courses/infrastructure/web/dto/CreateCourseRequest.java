package com.tecsup.lms.courses.infrastructure.web.dto;


@lombok.Data
public class CreateCourseRequest {
    private String title;
    private String description;
    private String instructor;
}