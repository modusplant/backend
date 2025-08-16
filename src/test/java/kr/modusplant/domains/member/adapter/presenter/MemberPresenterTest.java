package kr.modusplant.domains.member.adapter.presenter;

import kr.modusplant.domains.member.adapter.mapper.MemberMapperImpl;
import kr.modusplant.domains.member.adapter.mapper.supers.MemberMapper;
import kr.modusplant.domains.member.test.utils.adapter.MemberResponseTestUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberPresenterTest implements MemberTestUtils, MemberResponseTestUtils {
    private final MemberMapper memberMapper = new MemberMapperImpl();
    private final MemberPresenter memberPresenter = new MemberPresenter(memberMapper);

    @Test
    @DisplayName("presentMemberResponse로 응답 반환")
    void callPresentMemberResponse_withValidMember_returnsResponse() {
        assertThat(memberPresenter.presentMemberResponse(createMember())).isEqualTo(testMemberResponse);
    }
}