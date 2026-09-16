package kr.modusplant.infrastructure.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.shared.exception.enums.GeneralSuccessCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class WriteResponseLogoutSuccessHandlerTest {

    private final ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
    private final WriteResponseLogoutSuccessHandler handler = new WriteResponseLogoutSuccessHandler(objectMapper);

    @Test
    @DisplayName("로그아웃 성공 시 일반 성공 응답 작성 활동 수행")
    void testOnLogoutSuccess_givenAuthentication_willWriteGenericSuccessResponse() throws Exception {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        StringWriter stringWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(stringWriter));
        given(objectMapper.writeValueAsString(any())).willReturn("{\"status\":200}");

        // when
        handler.onLogoutSuccess(request, response, authentication);

        // then
        Mockito.verify(response).setStatus(GeneralSuccessCode.GENERIC_SUCCESS.getHttpStatus());
    }
}
