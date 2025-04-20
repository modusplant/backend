package kr.modusplant.global.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
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
    static final ThreadLocal<Boolean> isLogged = new ThreadLocal<>();

    @AfterThrowing(
            pointcut = "within(@org.springframework.stereotype.Service *)",
            throwing = "ex"
    )
    public void serviceLogException(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        StackTraceElement location = ex.getStackTrace()[0];
        String errorLocation = String.format("%s.%s(%s:%d)",
                location.getClassName(), location.getMethodName(), location.getFileName(), location.getLineNumber());

        HttpServletRequest request = ApiLoggingAspect.requestContext.get();  // REST API 요청(ThreadLocal 사용)
        if (request != null) {
            String callerUri = request.getRequestURI();
            String callerMethod = request.getMethod();
            String clientIp = request.getRemoteAddr();

            log.error("[BIZ ERROR] method={} | params={} | exception={} | message={} | location={}" +
                            "[BIZ ERROR - HTTP_REQUEST_INFO] httpMethod={} | uri={} | clientIp={}",
                    methodName, Arrays.toString(args), ex.getClass().getSimpleName(), ex.getMessage(), errorLocation,
                    callerUri, callerMethod, clientIp);
        } else {
            log.error("[BIZ ERROR] method={} | params={} | exception={} | message={} | location={}",
                    methodName, Arrays.toString(args), ex.getClass().getSimpleName(), ex.getMessage(), errorLocation);
        }

        if (isLogged.get() != Boolean.TRUE) {
            isLogged.set(Boolean.TRUE);
        }
    }

    public static void clearLoggedStatus() {
        isLogged.remove();
    }
}
