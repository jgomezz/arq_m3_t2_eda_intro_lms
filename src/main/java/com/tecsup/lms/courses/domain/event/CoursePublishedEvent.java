package com.tecsup.lms.courses.domain.event;

import com.tecsup.lms.shared.domain.event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CoursePublishedEvent extends DomainEvent {
    private  String courseId;
    private  String title;
    private  double price;

    public CoursePublishedEvent(String courseId, String title, double price) {
        super();
        this.courseId = courseId;
        this.title = title;
        this.price = price;
    }

    @Override
    public String getKey() {
        return this.courseId;
    }

}