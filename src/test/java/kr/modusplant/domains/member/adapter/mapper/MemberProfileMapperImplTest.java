package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberProfileTestUtils;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.shared.exception.InvalidValueException;
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
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponseWithImageUrlV2;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponseWithNullImagePath;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponseWithNullImageUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class MemberProfileMapperImplTest implements MemberProfileTestUtils {
    private final AmazonS3Service amazonS3Service = Mockito.mock(AmazonS3Service.class);
    private final MemberProfileMapper memberProfileMapper = new MemberProfileMapperImpl(amazonS3Service);

    @Test
    @DisplayName("버전 1과 이미지 경로가 있을 때 toMemberProfileResponse로 응답 반환")
    void testToMemberProfileResponse_givenVersion1AndImagePath_willReturnResponse() {
        // given
        given(amazonS3Service.generateS3SrcUrl(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_URL);

        // when & then
        assertThat(memberProfileMapper.toMemberProfileResponse(createMemberProfile(), 1)).isEqualTo(testMemberProfileResponseWithImageUrlV1);
    }

    @Test
    @DisplayName("버전 1과 이미지 경로가 없을 때 toMemberProfileResponse로 응답 반환")
    void testToMemberProfileResponse_givenVersion1AndNoImagePath_willReturnResponse() {
        // when
        assertThat(memberProfileMapper.toMemberProfileResponse(createMemberProfileWithoutImage(), 1)).isEqualTo(testMemberProfileResponseWithNullImageUrl);

        // then
        verify(amazonS3Service, never()).generateS3SrcUrl(any());
    }

    @Test
    @DisplayName("버전 2와 이미지 경로가 있을 때 toMemberProfileResponse로 응답 반환")
    void testToMemberProfileResponse_givenVersion2AndImagePath_willReturnResponse() {
        // when
        assertThat(memberProfileMapper.toMemberProfileResponse(createMemberProfile(), 2)).isEqualTo(testMemberProfileResponseWithImageUrlV2);

        // then
        verify(amazonS3Service, never()).generateS3SrcUrl(any());
    }

    @Test
    @DisplayName("버전 2와 이미지 경로가 없을 때 toMemberProfileResponse로 응답 반환")
    void testToMemberProfileResponse_givenVersion2AndNoImagePath_willReturnResponse() {
        // when & then
        assertThat(memberProfileMapper.toMemberProfileResponse(createMemberProfileWithoutImage(), 2)).isEqualTo(testMemberProfileResponseWithNullImageUrl);
    }

    @Test
    @DisplayName("버전 3과 이미지 경로가 있을 때 toMemberProfileResponse로 응답 반환")
    void testToMemberProfileResponse_givenVersion3AndImagePath_willReturnResponse() {
        // when
        assertThat(memberProfileMapper.toMemberProfileResponse(createMemberProfile(), 3)).isEqualTo(testMemberProfileResponseWithImagePathV3);

        // then
        verify(amazonS3Service, never()).generateS3SrcUrl(any());
    }

    @Test
    @DisplayName("버전 3과 이미지 경로가 없을 때 toMemberProfileResponse로 응답 반환")
    void testToMemberProfileResponse_givenVersion3AndNoImagePath_willReturnResponse() {
        // when & then
        assertThat(memberProfileMapper.toMemberProfileResponse(createMemberProfileWithoutImage(), 3)).isEqualTo(testMemberProfileResponseWithNullImagePath);
    }

    @Test
    @DisplayName("올바르지 않은 버전으로 toMemberProfileResponse를 호출하여 오류 발생")
    void testToMemberProfileResponse_givenInvalidVersion_willThrowException() {
        // when
        InvalidValueException exception = assertThrows(
                InvalidValueException.class, () -> memberProfileMapper.toMemberProfileResponse(createMemberProfile(), 4));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.INVALID_MEMBER_PROFILE_OVERRIDE_VERSION);
    }

    @Test
    @DisplayName("toMemberProfilePrepareResponse로 응답 반환")
    void testToMemberProfilePrepareResponse_givenValidData_willReturnResponse() {
        // when & then
        assertThat(memberProfileMapper.toMemberProfilePrepareResponse(testMemberProfileImagePath, MEMBER_PROFILE_BASIC_USER_IMAGE_STORAGE_URL)).isEqualTo(testMemberProfilePrepareResponse);
    }
}
