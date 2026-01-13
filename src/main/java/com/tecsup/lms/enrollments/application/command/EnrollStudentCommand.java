package com.tecsup.lms.enrollments.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnrollStudentCommand {
    private final String studentId;
    private final String studentName;
    private final String courseId;
}
