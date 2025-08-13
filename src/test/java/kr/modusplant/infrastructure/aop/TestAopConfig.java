package kr.modusplant.infrastructure.aop;

import org.aspectj.lang.JoinPoint;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestAopConfig {
    @Bean("serviceExceptionLoggingAspect")
    public ServiceExceptionLoggingAspect serviceExceptionLoggingAspect() {
        return new ServiceExceptionLoggingAspect() {
            @Override
            public void serviceLogException(JoinPoint joinPoint, Throwable ex) {
                // 스레드 검증 로직 생략
            }
        };
    }
}

