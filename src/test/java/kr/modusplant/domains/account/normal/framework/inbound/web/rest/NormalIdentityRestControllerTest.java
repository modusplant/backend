package kr.modusplant.domains.account.normal.framework.inbound.web.rest;

import kr.modusplant.domains.account.normal.adapter.controller.NormalIdentityController;
import kr.modusplant.domains.account.normal.common.util.usecase.request.EmailModificationRequestTestUtils;
import kr.modusplant.domains.account.normal.common.util.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.domains.account.normal.common.util.usecase.request.PasswordModificationRequestTestUtils;
import kr.modusplant.infrastructure.jwt.common.util.entity.RefreshTokenEntityTestUtils;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_ADMIN_UUID;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NormalIdentityRestControllerTest implements
        RefreshTokenEntityTestUtils, NormalSignUpRequestTestUtils,
        EmailModificationRequestTestUtils, PasswordModificationRequestTestUtils {

    private final NormalIdentityController controller = Mockito.mock(NormalIdentityController.class);
    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    private final NormalIdentityRestController restController = new NormalIdentityRestController(controller);

    @BeforeEach
    public void beforeEach() {
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshDuration", 604800000L);
    }

    @Test
    @DisplayName("유효한 요청을 받으면 일반 회원가입 후 성공 응답 반환")
    public void testRegisterNormalMember_givenValidRequest_willReturnSuccess() {
        // given & when
        ResponseEntity<DataResponse<Void>> response = restController.registerNormalMember(testNormalSignUpRequest);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("유효한 요청을 받으면 사용자의 이메일 갱신 후 성공 응답 반환")
    public void testModifyEmail_givenValidRequest_willReturnSuccess() {
        // given & when
        ResponseEntity<DataResponse<Void>> response =
                restController.modifyEmail(MEMBER_BASIC_USER_UUID, testEmailModificationRequest, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("본인이 아닌 회원의 이메일 갱신 시 예외 반환")
    public void testModifyEmail_givenNonSelfCaller_willThrowException() {
        // given & when & then
        assertThrows(AccessDeniedException.class,
                () -> restController.modifyEmail(MEMBER_BASIC_USER_UUID, testEmailModificationRequest, MEMBER_BASIC_ADMIN_UUID));
    }

    @Test
    @DisplayName("유효한 요청을 받으면 사용자의 비밀번호 갱신 후 성공 응답 반환")
    public void testModifyPassword_givenValidRequest_willReturnSuccess() {
        // given & when
        ResponseEntity<DataResponse<Void>> response =
                restController.modifyPassword(MEMBER_BASIC_USER_UUID, testPasswordModificationRequest, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("본인이 아닌 회원의 비밀번호 갱신 시 예외 반환")
    public void testModifyPassword_givenNonSelfCaller_willThrowException() {
        // given & when & then
        assertThrows(AccessDeniedException.class,
                () -> restController.modifyPassword(MEMBER_BASIC_USER_UUID, testPasswordModificationRequest, MEMBER_BASIC_ADMIN_UUID));
    }

}
