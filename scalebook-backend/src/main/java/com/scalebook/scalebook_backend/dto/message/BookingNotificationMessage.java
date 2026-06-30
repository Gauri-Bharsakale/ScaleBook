package com.scalebook.scalebook_backend.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingNotificationMessage implements java.io.Serializable {
    private Long bookingId;
    private String userEmail;
    private String resourceName;
}