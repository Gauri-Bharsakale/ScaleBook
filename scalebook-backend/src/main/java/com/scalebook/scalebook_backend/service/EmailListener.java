package com.scalebook.scalebook_backend.service;

import com.scalebook.scalebook_backend.config.RabbitMQConfig;
import com.scalebook.scalebook_backend.dto.message.BookingNotificationMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class EmailListener {

    @RabbitListener(queues = RabbitMQConfig.BOOKING_NOTIFICATION_QUEUE)
    public void handleBookingNotification(BookingNotificationMessage message) {
        // In a real project, integrate an email provider (SendGrid, AWS SES, etc.)
        // For now, simulate it:
        System.out.println("Sending confirmation email to " + message.getUserEmail() +
                " for resource: " + message.getResourceName());
    }
}
