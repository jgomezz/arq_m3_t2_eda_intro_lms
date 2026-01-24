package com.tecsup.lms.courses.domain.event;

import com.tecsup.lms.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CourseCreatedEvent extends DomainEvent {
    private  String courseId;
    private  String title;
    private  String instructor;

    public CourseCreatedEvent(String courseId, String title, String instructor) {
        super();
        this.courseId = courseId;
        this.title = title;
        this.instructor = instructor;
    }

    @Override
    public String getKey() {
        return this.courseId;
    }

}
