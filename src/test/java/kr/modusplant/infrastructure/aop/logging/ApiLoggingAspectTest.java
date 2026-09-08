package kr.modusplant.infrastructure.aop.logging;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @DisplayName("REST 컨트롤러 호출 시 API 로그 기록 활동 수행")
    void testTraceApiCall_givenRestControllerCall_willWriteApiLog() throws Exception {
        // given
        LogCaptor logCaptor = LogCaptor.forClass(ApiLoggingAspect.class);
        logCaptor.setLogLevelToInfo();

        // when
        mockMvc.perform(get("/api/admin/v1/monitor/monitor-success")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());

        // then
        boolean logFound = logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("method=GET") && log.contains("uri=/api/admin/v1/monitor/monitor-success"));
        assertThat(logFound).isTrue();
    }

    @Test
    @DisplayName("REST 컨트롤러 호출 시 OTel traceId 포함 API 로그 기록 활동 수행")
    void testTraceApiCall_givenRestControllerCall_willWriteOtelTraceIdInApiLog() throws Exception {
        // given
        LogCaptor logCaptor = LogCaptor.forClass(ApiLoggingAspect.class);
        logCaptor.setLogLevelToInfo();

        // when
        mockMvc.perform(get("/api/admin/v1/monitor/monitor-success")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());

        // then
        Pattern traceIdToken = Pattern.compile("traceId=(\\S+)");
        String restApiLog = logCaptor.getInfoLogs().stream()
                .filter(log -> log.contains("[REST API]"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("[REST API] 로그가 캡처되지 않았습니다"));

        Matcher matcher = traceIdToken.matcher(restApiLog);
        assertThat(matcher.find()).as("[REST API] 로그에 traceId 토큰이 있어야 함").isTrue();

        String traceId = matcher.group(1);
        // OTel/W3C 트레이스 ID: 하이픈 없는 32자리 소문자 hex. 기존의 UUID.randomUUID() 는 하이픈이 포함됨.
        assertThat(traceId)
                .as("traceId 는 OTel 트레이스 ID 형식이어야 하고 UUID 여서는 안 됨: <%s>", traceId)
                .doesNotContain("-")
                .matches("[0-9a-f]{32}");
    }
}
