package kr.modusplant.global.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * API 요청 및 응답 정보를 로깅하는 역할을 수행하는 AOP 클래스
 * 주요 기능:
 *      요청 URI, HTTP 메서드, 메소드명, 파라미터 로깅
 *      응답 시간 측정 및 출력
 *      예외 발생 시 로그 기록
 * 사용 용도 :
 *      운영 모니터링, 디버깅, API 사용량 분석 등
 */

@Aspect
@Order(1)
@Component
@Slf4j
public class ApiLoggingAspect {
    static final ThreadLocal<HttpServletRequest> requestContext = new ThreadLocal<>();
    private static final long SLOW_API_THRESHOLD_MS = 500;     // TODO : SLOW 쿼리 로깅의 기준은 변경가능

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object traceApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        requestContext.set(request);

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String methodName = joinPoint.getSignature().getName();
        String clientIp = request.getRemoteAddr();

        long startTime = System.nanoTime();
        Object result = joinPoint.proceed();
        long duration = System.nanoTime() - startTime;
        double durationInMs = duration / 1_000_000.0;
        String durationFormatted = String.format("%.2f", durationInMs);

        log.info("[REST API] method={} | uri={} | handler={} | ip={} | duration:{}ms"
                , method, uri, methodName, clientIp, durationFormatted);
        if (durationInMs > SLOW_API_THRESHOLD_MS) {
            log.warn("[SLOW API] method={} | uri={} | handler={} | duration:{}ms", method, uri, methodName, durationFormatted);
        }

        try {
            return result;
        } finally {
            requestContext.remove();
        }
    }
}