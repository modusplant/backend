package kr.modusplant.domains.member.framework.in.web.rest;

import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.adapter.response.MemberResponse;
import kr.modusplant.domains.member.test.utils.adapter.MemberRequestTestUtils;
import kr.modusplant.domains.member.test.utils.adapter.MemberResponseTestUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class MemberRestControllerTest implements MemberTestUtils, MemberRequestTestUtils, MemberResponseTestUtils {
    private final MemberController memberController = Mockito.mock(MemberController.class);
    private final MemberRestController memberRestController = new MemberRestController(memberController);

    @Test
    @DisplayName("registerMember로 응답 반환")
    void callRegisterMember_withValidRequest_returnsResponse() {
        // given
        given(memberController.register(testMemberRegisterRequest)).willReturn(testMemberResponse);

        // when
        ResponseEntity<MemberResponse> memberResponseEntity = memberRestController.registerMember(testMemberRegisterRequest);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getBody()).isEqualTo(testMemberResponse);
    }

    @Test
    @DisplayName("updateMemberNickname으로 응답 반환")
    void callUpdateMemberNickname_withValidRequest_returnsResponse() {
        // given
        given(memberController.updateNickname(testMemberNicknameUpdateRequest)).willReturn(testMemberResponse);

        // when
        ResponseEntity<MemberResponse> memberResponseEntity = memberRestController.updateMemberNickname(testMemberNicknameUpdateRequest);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getBody()).isEqualTo(testMemberResponse);
    }

}