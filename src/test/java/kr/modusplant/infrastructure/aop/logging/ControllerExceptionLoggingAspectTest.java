package kr.modusplant.infrastructure.aop.logging;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    @DisplayName("컨트롤러 예외 발생 시 에러 로그 기록 활동 수행")
    void testControllerLogException_givenControllerThrows_willWriteErrorLog() throws Exception {
        // given
        LogCaptor logCaptor = LogCaptor.forClass(ControllerExceptionLoggingAspect.class);
        logCaptor.setLogLevelToInfo();

        // when
        mockMvc.perform(get("/api/admin/v1/monitor/monitor-error-controller")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().is5xxServerError());

        // then
        boolean logFound = logCaptor.getErrorLogs().stream()
                .anyMatch(log -> log.contains("method=GET") && log.contains("uri=/api/admin/v1/monitor/monitor-error-controller"));
        assertThat(logFound).isTrue();
    }
}
