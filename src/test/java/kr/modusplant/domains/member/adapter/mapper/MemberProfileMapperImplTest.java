package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberProfileTestUtils;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_STORAGE_URL;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_URL;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImagePathTestUtils.testMemberProfileImagePath;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfilePrepareResponseTestUtils.testMemberProfilePrepareResponse;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponseWithImagePathV3;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponseWithImageUrlV1;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponseWithNullImagePath;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponseWithNullImageUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class MemberProfileMapperImplTest implements MemberProfileTestUtils {
    private final AmazonS3Service amazonS3Service = Mockito.mock(AmazonS3Service.class);
    private final MemberProfileMapper memberProfileMapper = new MemberProfileMapperImpl(amazonS3Service);

    @Test
    @DisplayName("저장소 URL 반환이 필요하고 이미지 경로가 있을 때 toMemberProfileResponse로 응답 반환")
    void testToMemberProfileResponse_givenNeedsToReturnStorageUrlTrueAndImagePath_willReturnResponse() {
        // given
        given(amazonS3Service.generateGetPresignedUrl(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_URL);

        // when & then
        assertThat(memberProfileMapper.toMemberProfileResponse(createMemberProfile(), true)).isEqualTo(testMemberProfileResponseWithImageUrlV1);
    }

    @Test
    @DisplayName("저장소 URL 반환이 필요하고 이미지 경로가 없을 때 toMemberProfileResponse로 응답 반환")
    void testToMemberProfileResponse_givenNeedsToReturnStorageUrlTrueAndNoImagePath_willReturnResponse() {
        // when
        assertThat(memberProfileMapper.toMemberProfileResponse(createMemberProfileWithoutImage(), true)).isEqualTo(testMemberProfileResponseWithNullImageUrl);

        // then
        verify(amazonS3Service, never()).generateGetPresignedUrl(any());
    }

    @Test
    @DisplayName("저장소 URL 반환이 필요하지 않고 이미지 경로가 있을 때 toMemberProfileResponse로 응답 반환")
    void testToMemberProfileResponse_givenNeedsToReturnStorageUrlFalseAndImagePath_willReturnResponse() {
        // when
        assertThat(memberProfileMapper.toMemberProfileResponse(createMemberProfile(), false)).isEqualTo(testMemberProfileResponseWithImagePathV3);

        // then
        verify(amazonS3Service, never()).generateGetPresignedUrl(any());
    }

    @Test
    @DisplayName("저장소 URL 반환이 필요하지 않고 이미지 경로가 없을 때 toMemberProfileResponse로 응답 반환")
    void testToMemberProfileResponse_givenNeedsToReturnStorageUrlFalseAndNoImagePath_willReturnResponse() {
        // when & then
        assertThat(memberProfileMapper.toMemberProfileResponse(createMemberProfileWithoutImage(), false)).isEqualTo(testMemberProfileResponseWithNullImagePath);
    }

    @Test
    @DisplayName("toMemberProfilePrepareResponse로 응답 반환")
    void testToMemberProfilePrepareResponse_givenValidData_willReturnResponse() {
        // when & then
        assertThat(memberProfileMapper.toMemberProfilePrepareResponse(testMemberProfileImagePath, MEMBER_PROFILE_BASIC_USER_IMAGE_STORAGE_URL)).isEqualTo(testMemberProfilePrepareResponse);
    }
}
