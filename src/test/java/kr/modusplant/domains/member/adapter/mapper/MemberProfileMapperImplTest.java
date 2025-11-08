package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.common.util.adapter.response.MemberProfileResponseTestUtils;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberProfileTestUtils;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberProfileMapperImplTest implements MemberProfileTestUtils, MemberProfileResponseTestUtils {
    private final MemberProfileMapper memberProfileMapper = new MemberProfileMapperImpl();

    @Test
    @DisplayName("toMemberResponse로 응답 반환")
    void testToMemberResponse_givenValidMember_willReturnResponse() {
        assertThat(memberProfileMapper.toMemberProfileResponse(createMemberProfile())).isEqualTo(testMemberProfileResponse);
    }
}