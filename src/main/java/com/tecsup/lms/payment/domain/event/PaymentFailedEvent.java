package com.tecsup.lms.payment.domain.event;

import com.tecsup.lms.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent extends DomainEvent {

    private String enrollmentId;
    private String reason;
    private String errorCode;
    private LocalDateTime timestamp;

    @Override
    public String getKey() {
        return enrollmentId;
    }
}
