package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.common.utils.adapter.request.MemberRequestTestUtils;
import kr.modusplant.domains.member.common.utils.adapter.response.MemberResponseTestUtils;
import kr.modusplant.domains.member.common.utils.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberMapperImplTest implements MemberTestUtils, MemberRequestTestUtils, MemberResponseTestUtils {
    private final MemberMapper memberMapper = new MemberMapperImpl();

    @Test
    @DisplayName("toMember로 닉네임으로부터 회원 반환")
    void testToNickname_givenValidNickname_willReturnNickname() {
        assertThat(memberMapper.toMember(testMemberNickname).getMemberNickname()).isEqualTo(testMemberNickname);
    }

    @Test
    @DisplayName("toMember로 닉네임 갱신 요청으로부터 회원 반환")
    void testToMember_givenValidNicknameUpdateRequest_willReturnMember() {
        assertThat(memberMapper.toMember(testMemberNicknameUpdateRequest)).isEqualTo(createMember());
    }

    @Test
    @DisplayName("toMemberResponse로 응답 반환")
    void testToMemberResponse_givenValidMember_willReturnResponse() {
        assertThat(memberMapper.toMemberResponse(createMember())).isEqualTo(testMemberResponse);
    }
}