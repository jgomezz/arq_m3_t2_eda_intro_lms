package com.tecsup.lms.admin.infrastructure.web.controller;

import com.tecsup.lms.admin.infrastructure.web.dto.DLQResponse;
import com.tecsup.lms.shared.infrastructure.dlq.DeadLetterQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dlq")
@RequiredArgsConstructor
public class DLQController {

    private final DeadLetterQueue deadLetterQueue;

    @GetMapping
    public ResponseEntity<DLQResponse>  getFailedEvents() {
        DLQResponse response = new DLQResponse();
        response.setEvents(deadLetterQueue.getFailedEvents());
        return ResponseEntity.ok(response);
    }

}
