package kr.modusplant.infrastructure.config.async;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /* 기본 executor(executor 미지정 시) : 플랫폼 스레드 풀 */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }

    // 비동기 예외 처리
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    /* 회원 도메인 전용 executor : 가상 스레드 */
    @Bean(name = "memberDomainExecutor")
    public Executor memberDomainExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("virtual-domain-member-");
        executor.setVirtualThreads(true);
        return executor;
    }

    /* 소셜 도메인 전용 executor : 가상 스레드 */
    @Bean(name = "socialDomainExecutor")
    public Executor socialDomainExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("virtual-domain-social-");
        executor.setVirtualThreads(true);
        return executor;
    }

    /* AWS 전용 executor : 가상 스레드 */
    @Bean(name = "awsExecutor")
    public Executor awsExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("virtual-aws-");
        executor.setVirtualThreads(true);
        return executor;
    }

    /* Redis 전용 executor : 가상 스레드 */
    @Bean(name = "redisExecutor")
    public Executor redisExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("virtual-redis-");
        executor.setVirtualThreads(true);
        return executor;
    }

    /* JWT 전용 executor : 가상 스레드 */
    @Bean(name = "jwtExecutor")
    public Executor jwtExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("virtual-jwt-");
        executor.setVirtualThreads(true);
        return executor;
    }

    /* 알림 전용 executor : 가상 스레드 */
    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("virtual-notification-");
        executor.setVirtualThreads(true);
        return executor;
    }
}
