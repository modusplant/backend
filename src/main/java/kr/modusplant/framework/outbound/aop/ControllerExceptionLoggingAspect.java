package kr.modusplant.framework.outbound.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * API 컨트롤러 예외 상황을 로깅하는 역할을 수행하는 AOP 클래스
 * 주요 기능:
 * HTTP 요청과 관련된 예외 발생 시 로그 기록
 * 사용 용도:
 * API 예외 발생 상황 분석, 모니터링을 통한 핫픽스 등
 */
@Order(3)
@Aspect
@Component
@Slf4j
public class ControllerExceptionLoggingAspect {

    @AfterThrowing(
            pointcut = "within(@org.springframework.web.bind.annotation.RestController *)",
            throwing = "ex"
    )
    public void controllerLogException(JoinPoint joinPoint, Throwable ex) {
        if ("true".equals(MDC.get("isLogged"))) {
            return;
        }

        String methodName = joinPoint.getSignature().getName();
        StackTraceElement location = ex.getStackTrace()[0];
        String fileName = location.getFileName() != null ? location.getFileName() : "UnknownFile";
        int lineNumber = location.getLineNumber() >= 0 ? location.getLineNumber() : -1;

        String errorLocation = String.format("%s.%s(%s:%d)",
                location.getClassName(), location.getMethodName(), fileName, lineNumber);

        String traceId = MDC.get("traceId") != null ? MDC.get("traceId") : "N/A";
        String uri = MDC.get("uri") != null ? MDC.get("uri") : "N/A";
        String method = MDC.get("method") != null ? MDC.get("method") : "N/A";
        String clientIp = MDC.get("clientIp") != null ? MDC.get("clientIp") : "UNKNOWN";

        log.error("[REST ERROR] traceId={}, method={} | uri={} | handler={} | ip={} | exception={} | message={} | location={} ",
                traceId, method, uri, methodName, clientIp, ex.getClass().getSimpleName(), ex.getMessage(), errorLocation);
    }
}
