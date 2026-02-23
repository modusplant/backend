package kr.modusplant.infrastructure.aop.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 서비스 레벨에서 발생한 예외를 로깅하는 역할을 수행하는 AOP 클래스
 * 주요 기능:
 * 비즈니스 로직에서 발생한 예외를 로깅
 * 사용 용도:
 * 비즈니스 로직 예외 발생 상황 분석
 */
@Order(2)
@Aspect
@Component
@Slf4j
public class ServiceExceptionLoggingAspect {

    @AfterThrowing(
            pointcut = "within(@org.springframework.stereotype.Service *)",
            throwing = "ex"
    )
    public void serviceLogException(JoinPoint joinPoint, Throwable ex) {
        if (Boolean.TRUE.toString().equals(MDC.get("isLogged"))) return;

        if (!ApiLoggingAspect.isSameThread()) {
            log.warn("[THREAD MISMATCH] 컨트롤러, 서비스 AOP 스레드 불일치");    // TODO : 추후 비동기, 멀티스레드 등의 불일치 해결을 위한 로깅
        } else {
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            StackTraceElement location = ex.getStackTrace()[0];
            String fileName = location.getFileName() != null ? location.getFileName() : "UnknownFile";
            int lineNumber = location.getLineNumber() >= 0 ? location.getLineNumber() : -1;

            String errorLocation = String.format("%s.%s(%s:%d)",
                    location.getClassName(), location.getMethodName(), fileName, lineNumber);

            String traceId = MDC.get("traceId") != null ? MDC.get("traceId") : "N/A";
            String clientIp = MDC.get("clientIp") != null ? MDC.get("clientIp") : "UNKNOWN";
            String uri = MDC.get("uri") != null ? MDC.get("uri") : "N/A";
            String method = MDC.get("method") != null ? MDC.get("method") : "N/A";

            log.error("[BIZ ERROR] traceId={} | method={} | params={} | exception={} | message={} | location={}\n" +
                            "[BIZ ERROR - HTTP_REQUEST_INFO] httpMethod={} | uri={} | clientIp={}",
                    traceId, methodName, Arrays.toString(args), ex.getClass().getSimpleName(), ex.getMessage(), errorLocation,
                    uri, method, clientIp);
        }
        MDC.put("isLogged", Boolean.TRUE.toString());
    }
}
