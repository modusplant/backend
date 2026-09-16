package kr.modusplant.infrastructure.security.util;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class SecurityResponseUtilsTest {

    @Test
    @DisplayName("상태 코드와 본문으로 JSON 응답 작성 활동 수행")
    void testWriteResponse_givenStatusAndBody_willWriteJsonResponse() throws Exception {
        // given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(stringWriter));

        // when
        SecurityResponseUtils.writeResponse(response, 200, "{\"status\":200}");

        // then
        Mockito.verify(response).setStatus(200);
        Mockito.verify(response).setCharacterEncoding("UTF-8");
        Mockito.verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        assertThat(stringWriter.toString()).isEqualTo("{\"status\":200}");
    }
}
