package com.scalebook.scalebook_backend.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingRequest {

    @NotNull
    private Long resourceId;

    @NotNull
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
}
