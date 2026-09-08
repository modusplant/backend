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
    @DisplayName("AOP 적용 컨트롤러 메소드 호출")
    void getMonitorSuccess_givenRestController_willReturnSuccessStatusWithAopLogging() throws Exception{
        LogCaptor logCaptor = LogCaptor.forClass(ApiLoggingAspect.class);
        logCaptor.setLogLevelToInfo();
        mockMvc.perform(get("/api/admin/v1/monitor/monitor-success")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());

        // then
        boolean logFound = logCaptor.getInfoLogs().stream()
                        .anyMatch(log -> log.contains("method=GET") && log.contains("uri=/api/admin/v1/monitor/monitor-success"));
        assertThat(logFound).isTrue();
    }

    @Test
    @DisplayName("[REST API] 로그의 traceId 는 랜덤 UUID 가 아니라 Micrometer/OTel 트레이스 ID(32-hex) 다")
    void restApiLog_traceId_isOtelTraceIdNotRandomUuid() throws Exception {
        LogCaptor logCaptor = LogCaptor.forClass(ApiLoggingAspect.class);
        logCaptor.setLogLevelToInfo();

        mockMvc.perform(get("/api/admin/v1/monitor/monitor-success")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());

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
