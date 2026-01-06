package com.tecsup.lms.courses.domain.event;

import com.tecsup.lms.shared.domain.event.DomainEvent;
import lombok.Getter;

@Getter
public class CoursePublishedEvent extends DomainEvent {
    private final String courseId;
    private final String title;
    private final double price;

    public CoursePublishedEvent(String courseId, String title, double price) {
        super();
        this.courseId = courseId;
        this.title = title;
        this.price = price;
    }
}