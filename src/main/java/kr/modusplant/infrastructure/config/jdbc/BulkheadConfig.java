package kr.modusplant.infrastructure.config.jdbc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Semaphore;

@Configuration
public class BulkheadConfig {

    @Value("${app.semaphore.datasource.bulkhead.notification.connection-size}")
    private int notificationBulkheadSize;

    @Value("${app.semaphore.datasource.bulkhead.admin.connection-size}")
    private int adminBulkheadSize;

    @Bean(name = "notificationSemaphore")
    public Semaphore notificationSemaphore() {
        return new Semaphore(notificationBulkheadSize, true);
    }

    @Bean(name = "adminSemaphore")
    public Semaphore adminSemaphore() {
        return new Semaphore(adminBulkheadSize, true);
    }
}
