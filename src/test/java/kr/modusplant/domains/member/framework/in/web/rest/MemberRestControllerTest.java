package kr.modusplant.domains.member.framework.in.web.rest;

import kr.modusplant.domains.member.adapter.in.response.MemberResponse;
import kr.modusplant.domains.member.application.service.MemberService;
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
    private final MemberService memberService = Mockito.mock(MemberService.class);
    private final MemberRestController memberRestController = new MemberRestController(memberService);

    @Test
    @DisplayName("registerMember로 응답 반환")
    void callRegisterMember_withValidRequest_returnsResponse() {
        // given
        given(memberService.register(testMemberRegisterRequest)).willReturn(testMemberResponse);

        // when
        ResponseEntity<MemberResponse> memberResponseEntity = memberRestController.registerMember(testMemberRegisterRequest);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getBody()).isEqualTo(testMemberResponse);
    }
}