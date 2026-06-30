package com.scalebook.scalebook_backend.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Spring Boot auto-configures Redis as the cache backend
    // because we already have spring-boot-starter-data-redis on the classpath
}
