package kr.modusplant.domains.member.adapter.in.mapper;

import kr.modusplant.domains.member.adapter.mapper.MemberMapperImpl;
import kr.modusplant.domains.member.common.utils.adapter.MemberRequestTestUtils;
import kr.modusplant.domains.member.common.utils.adapter.MemberResponseTestUtils;
import kr.modusplant.domains.member.common.utils.domain.MemberTestUtils;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberMapperImplTest implements MemberTestUtils, MemberRequestTestUtils, MemberResponseTestUtils {
    private final MemberMapper memberMapper = new MemberMapperImpl();

    @Test
    @DisplayName("toNickname으로 닉네임 반환")
    void callToNickname_withValidRegisterRequest_returnsNickname() {
        assertThat(memberMapper.toNickname(testMemberRegisterRequest)).isEqualTo(testMemberNickname);
    }

    @Test
    @DisplayName("toMember로 회원 반환")
    void callToMember_withValidNicknameUpdateRequest_returnsMember() {
        assertThat(memberMapper.toMember(testMemberNicknameUpdateRequest)).isEqualTo(createMember());
    }

    @Test
    @DisplayName("toMemberResponse로 응답 반환")
    void callToMemberResponse_withValidMember_returnsResponse() {
        assertThat(memberMapper.toMemberResponse(createMember())).isEqualTo(testMemberResponse);
    }
}