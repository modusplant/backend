package kr.modusplant.infrastructure.aop;

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
public class ControllerExceptionLoggingAspectTest {
    private final MockMvc mockMvc;

    @Autowired
    ControllerExceptionLoggingAspectTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("AOP 적용 컨트롤러 메소드 예외 상황 로깅")
    void getMonitorControllerError_withRestController_returnErrorStatusWithAopLogging() throws Exception{
        LogCaptor logCaptor = LogCaptor.forClass(ControllerExceptionLoggingAspect.class);
        logCaptor.setLogLevelToInfo();
        mockMvc.perform(get("/api/monitor/monitor-error-controller")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().is5xxServerError());

        // then
        boolean logFound = logCaptor.getErrorLogs().stream()
                .anyMatch(log -> log.contains("method=GET") && log.contains("uri=/api/monitor/monitor-error-controller"));
        assertThat(logFound).isTrue();
    }
}
