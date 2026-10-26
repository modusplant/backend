package kr.modusplant.domains.account.identity.framework.inbound.web.rest;

import kr.modusplant.domains.account.identity.adapter.controller.IdentityController;
import kr.modusplant.domains.account.identity.common.util.usecase.response.IdentityAuthResponseTestUtils;
import kr.modusplant.domains.account.identity.usecase.response.IdentityAuthResponse;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_ADMIN_UUID;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.infrastructure.security.enums.SecurityErrorCode.AUTHORIZATION_FAILED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class IdentityRestControllerTest implements IdentityAuthResponseTestUtils {
    private final IdentityController controller = Mockito.mock(IdentityController.class);
    private final IdentityRestController restController = new IdentityRestController(controller);

    @Test
    @DisplayName("본인의 인증 정보 조회 시 ResponseEntity 반환")
    public void testGetAuthInfo_givenSelfCaller_willReturnResponseEntity() {
        // given
        given(controller.getAuthInfo(MEMBER_BASIC_USER_UUID)).willReturn(testIdentityAuthResponse);

        // when
        ResponseEntity<DataResponse<IdentityAuthResponse>> response =
                restController.getAuthInfo(MEMBER_BASIC_USER_UUID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getData()).isEqualTo(testIdentityAuthResponse);
        verify(controller).getAuthInfo(MEMBER_BASIC_USER_UUID);
    }

    @Test
    @DisplayName("본인이 아닌 회원의 인증 정보 조회 시 예외 반환")
    public void testGetAuthInfo_givenNonSelfCaller_willThrowException() {
        // given & when & then
        NotAccessibleException exception = assertThrows(NotAccessibleException.class,
                () -> restController.getAuthInfo(MEMBER_BASIC_USER_UUID, MEMBER_BASIC_ADMIN_UUID));

        assertThat(exception.getErrorCode()).isEqualTo(AUTHORIZATION_FAILED);
        verify(controller, never()).getAuthInfo(MEMBER_BASIC_USER_UUID);
    }
}
