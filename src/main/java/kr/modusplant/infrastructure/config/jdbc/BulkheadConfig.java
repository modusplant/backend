package kr.modusplant.infrastructure.config.jdbc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Semaphore;

@Configuration
public class BulkheadConfig {

    @Value("${app.semaphore.bulkhead.notification.connection-size}")
    private int notificationBulkheadSize;

    @Bean(name = "notificationSemaphore")
    public Semaphore notificationSemaphore() {
        return new Semaphore(notificationBulkheadSize, true);
    }

}
