package kr.modusplant.infrastructure.aop;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * API 요청 및 응답 정보를 로깅하는 역할을 수행하는 AOP 클래스
 * 주요 기능:
 * 요청 URI, HTTP 메서드, 메소드명, 파라미터 로깅
 * 응답 시간 측정 및 출력
 * 예외 발생 시 로그 기록
 * 사용 용도 :
 * 운영 모니터링, 디버깅, API 사용량 분석 등
 */

@Aspect
@Order(1)
@Component
@Slf4j
public class ApiLoggingAspect {
    private static final ThreadLocal<Long> THREAD_ID = new ThreadLocal<>();
    private static final long SLOW_API_THRESHOLD_MS = 500;     // TODO : SLOW 쿼리 로깅의 기준은 변경 가능
    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_SPAN_ID  = "spanId";

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object traceApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        THREAD_ID.set(Thread.currentThread().threadId());

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs.getRequest();

        SpanContext sc = Span.current().getSpanContext();
        final String traceId = sc.isValid() ? sc.getTraceId() : "none";
        final String spanId  = sc.isValid() ? sc.getSpanId()  : "none";

        MDC.put("uri", request.getRequestURI());
        MDC.put("method", request.getMethod());
        MDC.put("clientIp", getClientIp(request));
        MDC.put(MDC_TRACE_ID, traceId); // 추적용 ID
        MDC.put(MDC_SPAN_ID, spanId); // 추적용 ID
        MDC.put("methodName", joinPoint.getSignature().getName());
        MDC.put("isLogged", Boolean.FALSE.toString()); // 예외 로깅 중복 방지용

        long startTime = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long duration = System.nanoTime() - startTime;
            double durationInMs = duration / 1_000_000.0;
            String durationFormatted = String.format("%.2f", durationInMs);

            log.info("[REST API] traceId={} | spanId={} | method={} | uri={} | handler={} | ip={} | duration:{}ms"
                    , MDC.get(MDC_TRACE_ID), MDC.get(MDC_SPAN_ID), MDC.get("method"), MDC.get("uri"), MDC.get("methodName"), MDC.get("clientIp"), durationFormatted);
            if (durationInMs > SLOW_API_THRESHOLD_MS) {
                log.warn("[SLOW API] traceId={} | spanId={} | duration:{}ms", MDC.get(MDC_TRACE_ID), MDC.get(MDC_SPAN_ID), durationFormatted);
            }

            return result;
        } finally {
            THREAD_ID.remove();
            MDC.clear();
        }
    }

    public static boolean isSameThread() {
        Long original = THREAD_ID.get();
        return original != null && original.equals(Thread.currentThread().threadId());
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}