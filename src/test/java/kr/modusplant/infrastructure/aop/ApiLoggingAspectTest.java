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
public class ApiLoggingAspectTest {
    private final MockMvc mockMvc;

    @Autowired
    ApiLoggingAspectTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("AOP 적용 컨트롤러 메소드 호출")
    void getMonitorSuccess_givenRestController_willReturnSuccessStatusWithAopLogging() throws Exception{
        LogCaptor logCaptor = LogCaptor.forClass(ApiLoggingAspect.class);
        logCaptor.setLogLevelToInfo();
        mockMvc.perform(get("/api/monitor/monitor-success")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());

        // then
        boolean logFound = logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("method=GET") && log.contains("uri=/api/monitor/monitor-success"));
        assertThat(logFound).isTrue();
    }
}
