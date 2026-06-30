package com.scalebook.scalebook_backend.service;

import com.scalebook.scalebook_backend.config.RabbitMQConfig;
import com.scalebook.scalebook_backend.dto.message.BookingNotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;

    public void queueBookingConfirmation(Long bookingId, String userEmail, String resourceName) {
        BookingNotificationMessage message =
                new BookingNotificationMessage(bookingId, userEmail, resourceName);

        rabbitTemplate.convertAndSend(RabbitMQConfig.BOOKING_NOTIFICATION_QUEUE, message);
    }
}
