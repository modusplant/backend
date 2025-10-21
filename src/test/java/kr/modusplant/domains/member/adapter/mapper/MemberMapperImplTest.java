package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.common.util.adapter.response.MemberResponseTestUtils;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberMapperImplTest implements MemberTestUtils, MemberResponseTestUtils {
    private final MemberMapper memberMapper = new MemberMapperImpl();

    @Test
    @DisplayName("toMemberResponse로 응답 반환")
    void testToMemberResponse_givenValidMember_willReturnResponse() {
        assertThat(memberMapper.toMemberResponse(createMember())).isEqualTo(testMemberResponse);
    }
}