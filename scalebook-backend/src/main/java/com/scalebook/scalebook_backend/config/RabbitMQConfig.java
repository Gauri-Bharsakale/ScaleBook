package com.scalebook.scalebook_backend.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String BOOKING_NOTIFICATION_QUEUE = "booking-notification-queue";

    @Bean
    public Queue bookingNotificationQueue() {
        return new Queue(BOOKING_NOTIFICATION_QUEUE, true); // true = durable, survives broker restart
    }
}
