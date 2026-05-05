package kr.modusplant.infrastructure.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.DefaultAuthenticationEntryPoint;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.util.SecurityLogger;
import kr.modusplant.infrastructure.security.util.SecurityResponseUtils;
import kr.modusplant.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class SecurityExceptionHandlingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final DefaultAuthenticationEntryPoint entryPoint;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            entryPoint.commence(request, response, ex);
        } catch (BusinessException ex) {
            isResponseCommitted(response, ex);
            writeBusinessErrorResponse(response, ex);
        } catch (Exception ex) {
            isResponseCommitted(response, ex);
            writeGeneralErrorResponse(response, ex);
        }
    }

    private void isResponseCommitted(HttpServletResponse response, Exception exception) {
        if (response.isCommitted()) {
            log.warn("Response already committed, cannot write error for: {}", exception.getMessage());
        }
    }

    private void writeBusinessErrorResponse(HttpServletResponse response, BusinessException businessEx) throws IOException {
        SecurityResponseUtils.writeResponse(
                response, businessEx.getErrorCode().getHttpStatus(),
                objectMapper.writeValueAsString(DataResponse
                        .of(businessEx.getErrorCode()))
                );
    }

    private void writeGeneralErrorResponse(HttpServletResponse response, Exception ex) throws IOException {
        SecurityLogger.logUnknownException(ex);
        SecurityResponseUtils.writeResponse(
                response, SecurityErrorCode.AUTHENTICATION_FAILED.getHttpStatus(),
                objectMapper.writeValueAsString(DataResponse
                        .of(SecurityErrorCode.AUTHENTICATION_FAILED))
        );
    }
}
