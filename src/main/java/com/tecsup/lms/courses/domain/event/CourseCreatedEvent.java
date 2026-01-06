package com.tecsup.lms.courses.domain.event;

import com.tecsup.lms.shared.domain.event.DomainEvent;
import lombok.Getter;

@Getter
public class CourseCreatedEvent extends DomainEvent {
    private final String courseId;
    private final String title;
    private final String instructor;

    public CourseCreatedEvent(String courseId, String title, String instructor) {
        super();
        this.courseId = courseId;
        this.title = title;
        this.instructor = instructor;
    }
}
