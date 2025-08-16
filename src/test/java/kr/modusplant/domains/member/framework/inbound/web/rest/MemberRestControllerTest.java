package kr.modusplant.domains.member.framework.inbound.web.rest;

import kr.modusplant.domains.member.adapter.presenter.MemberPresenter;
import kr.modusplant.domains.member.adapter.response.MemberResponse;
import kr.modusplant.domains.member.adapter.service.MemberService;
import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.test.utils.adapter.MemberRequestUtils;
import kr.modusplant.domains.member.test.utils.adapter.MemberResponseUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class MemberRestControllerTest implements MemberUtils, MemberRequestUtils, MemberResponseUtils {
    private final MemberService memberService = Mockito.mock(MemberService.class);
    private final MemberPresenter memberPresenter = Mockito.mock(MemberPresenter.class);
    private final MemberRestController memberRestController = new MemberRestController(memberService, memberPresenter);

    @Test
    @DisplayName("registerMember로 응답 반환")
    void callRegisterMember_withValidRequest_returnsResponse() {
        // given
        Member member = createMember();
        given(memberService.register(testMemberRegisterRequest)).willReturn(member);
        given(memberPresenter.presentMemberResponse(member)).willReturn(testMemberResponse);

        // when
        ResponseEntity<MemberResponse> memberResponseEntity = memberRestController.registerMember(testMemberRegisterRequest);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getBody()).isEqualTo(testMemberResponse);
    }
}