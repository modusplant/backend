package kr.modusplant.global.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * API 컨트롤러 예외 상황을 로깅하는 역할을 수행하는 AOP 클래스
 * 주요 기능:
 *      HTTP 요청과 관련된 예외 발생 시 로그 기록
 * 사용 용도:
 *      API 예외 발생 상황 분석, 모니터링을 통한 핫픽스 등
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
        if (ServiceExceptionLoggingAspect.isLogged.get() == Boolean.TRUE) {
            return;
        }

        String methodName = joinPoint.getSignature().getName();
        StackTraceElement location = ex.getStackTrace()[0];
        String errorLocation = String.format("%s.%s(%s:%d)",
                location.getClassName(), location.getMethodName(), location.getFileName(), location.getLineNumber());

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String clientIp = request.getRemoteAddr();

            log.error("[REST ERROR] method={} | uri={} | handler={} | ip={} | exception={} | message={} location={} ",
                    method, uri, methodName, clientIp, ex.getClass().getSimpleName(), ex.getMessage(), errorLocation);
        } else {
            log.error("[REST ERROR] handler={} | exception={} | message={} location={} (Request info unavailable)",
                    methodName, ex.getClass().getSimpleName(), ex.getMessage(), errorLocation);
        }
    }
}
