package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberProfileTestUtils;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponse;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberProfileMapperImplTest implements MemberProfileTestUtils {
    private final AmazonS3Service amazonS3Service = Mockito.mock(AmazonS3Service.class);
    private final MemberProfileMapper memberProfileMapper = new MemberProfileMapperImpl(amazonS3Service);

    @Test
    @DisplayName("toMemberResponse로 응답 반환")
    void testToMemberResponse_givenValidMember_willReturnResponse() {
        // given & when
        given(amazonS3Service.generateS3SrcUrl(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_URL);

        // then
        assertThat(memberProfileMapper.toMemberProfileResponse(createMemberProfile())).isEqualTo(testMemberProfileResponse);
    }
}