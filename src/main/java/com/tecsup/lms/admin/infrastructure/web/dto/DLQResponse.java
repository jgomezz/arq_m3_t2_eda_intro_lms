package com.tecsup.lms.admin.infrastructure.web.dto;

import com.tecsup.lms.shared.infrastructure.dlq.FailedEvent;
import lombok.Data;

import java.util.List;

@Data
public class DLQResponse {
    private List<FailedEvent> events;
}
