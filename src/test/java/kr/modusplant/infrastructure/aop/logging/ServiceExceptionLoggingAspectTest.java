package kr.modusplant.infrastructure.aop.logging;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ServiceExceptionLoggingAspectTest {
    private final MockMvc mockMvc;

    @Autowired
    ServiceExceptionLoggingAspectTest(MockMvc mockMvc) { this.mockMvc = mockMvc; }

    @Test
    @DisplayName("서비스 RuntimeException 발생 시 SYS ERROR 로그 기록 활동 수행")
    void testServiceLogException_givenRuntimeException_willWriteSysErrorLog() throws Exception {
        // given
        LogCaptor logCaptor = LogCaptor.forClass(ServiceExceptionLoggingAspect.class);
        logCaptor.setLogLevelToInfo();

        // when
        mockMvc.perform(get("/test/monitor/sys-error")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().is5xxServerError());

        // then
        boolean logFound = logCaptor.getErrorLogs().stream()
                .anyMatch(log -> log.contains("[SYS ERROR]")
                        && log.contains("method=throwSysError")
                        && log.contains("exception=RuntimeException")
                        && log.contains("httpMethod=GET")
                        && log.contains("uri=/test/monitor/sys-error"));
        assertThat(logFound).isTrue();
    }

    @Test
    @DisplayName("서비스 BusinessException 발생 시 BIZ ERROR 로그 기록 활동 수행")
    void testServiceLogException_givenBusinessException_willWriteBizErrorLog() throws Exception {
        // given
        LogCaptor logCaptor = LogCaptor.forClass(ServiceExceptionLoggingAspect.class);
        logCaptor.setLogLevelToInfo();

        // when
        mockMvc.perform(get("/test/monitor/biz-error")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().is5xxServerError());

        // then
        boolean logFound = logCaptor.getErrorLogs().stream()
                .anyMatch(log -> log.contains("[BIZ ERROR]")
                        && log.contains("errorCode=internal_server_error")
                        && log.contains("method=throwBizError")
                        && log.contains("exception=BusinessException")
                        && log.contains("httpMethod=GET")
                        && log.contains("uri=/test/monitor/biz-error"));
        assertThat(logFound).isTrue();
    }

    @Test
    @DisplayName("cause 포함 BusinessException 발생 시 causeMessage 포함 BIZ ERROR 로그 기록 활동 수행")
    void testServiceLogException_givenBusinessExceptionWithCause_willWriteBizErrorLogWithCauseMessage() throws Exception {
        // given
        LogCaptor logCaptor = LogCaptor.forClass(ServiceExceptionLoggingAspect.class);
        logCaptor.setLogLevelToInfo();

        // when
        mockMvc.perform(get("/test/monitor/biz-error-with-cause")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().is5xxServerError());

        // then
        boolean logFound = logCaptor.getErrorLogs().stream()
                .anyMatch(log -> log.contains("[BIZ ERROR]")
                        && log.contains("errorCode=internal_server_error")
                        && log.contains("method=throwBizErrorWithCause")
                        && log.contains("causeMessage=테스트용 원인 예외")
                        && log.contains("httpMethod=GET")
                        && log.contains("uri=/test/monitor/biz-error-with-cause"));
        assertThat(logFound).isTrue();
    }
}
