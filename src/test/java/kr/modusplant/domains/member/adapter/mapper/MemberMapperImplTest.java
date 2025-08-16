package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.adapter.mapper.supers.MemberMapper;
import kr.modusplant.domains.member.test.utils.adapter.MemberRequestUtils;
import kr.modusplant.domains.member.test.utils.adapter.MemberResponseUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberMapperImplTest implements MemberUtils, MemberRequestUtils, MemberResponseUtils {
    private final MemberMapper memberMapper = new MemberMapperImpl();

    @Test
    @DisplayName("toNickname으로 닉네임 반환")
    void callToNickname_withValidRequest_returnsNickname() {
        assertThat(memberMapper.toNickname(testMemberRegisterRequest)).isEqualTo(testNickname);
    }

    @Test
    @DisplayName("toMemberResponse로 응답 반환")
    void callToMemberResponse_withValidMember_returnsResponse() {
        assertThat(memberMapper.toMemberResponse(createMember())).isEqualTo(testMemberResponse);
    }
}