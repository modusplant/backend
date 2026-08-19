package kr.modusplant.domains.member.adapter.helper;

import kr.modusplant.domains.member.usecase.record.MemberProfileOverrideRecord_V1;
import kr.modusplant.infrastructure.file.service.PendingFileService;
import kr.modusplant.shared.framework.aws.exception.NotFoundFileKeyOnS3Exception;
import kr.modusplant.shared.framework.aws.exception.enums.AWSErrorCode;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_CONTENT_TYPE;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_STORAGE_URL;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_INTRODUCTION;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_IMAGES;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_IMAGES_WITH_NULL;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_IMAGE_CONTENT_TYPE;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATHS;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_STORAGE_URL_1;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImagePathTestUtils.testMemberProfileImagePath;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImagePathTestUtils.testReportImagePath1;
import static kr.modusplant.domains.member.common.util.domain.vo.nullobject.EmptyMemberProfileImagePathTestUtils.testEmptyMemberProfileImagePath;
import static kr.modusplant.domains.member.common.util.domain.vo.nullobject.EmptyReportImagePathTestUtils.testEmptyReportImagePath;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileOverrideRecordTestUtils.testMemberProfileOverrideRecordV1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MemberImageIOHelperTest {
    private final AmazonS3Service amazonS3Service = Mockito.mock(AmazonS3Service.class);
    private final PendingFileService pendingFileService = Mockito.mock(PendingFileService.class);
    private final MemberImageIOHelper memberImageIOHelper = new MemberImageIOHelper(amazonS3Service, pendingFileService);

    @Test
    @DisplayName("uploadImage를 통해 회원 프로필 이미지 업로드")
    void testUploadImage_givenMemberProfileImage_willReturnImagePath() throws IOException {
        // given
        willDoNothing().given(amazonS3Service).uploadFile(any(), any());

        // when
        String imagePath = memberImageIOHelper.uploadImage(testMemberId, testMemberProfileOverrideRecordV1);

        // then
        assertThat(imagePath).isEqualTo(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH);
    }

    @Test
    @DisplayName("issueStorageUrl을 통해 회원 프로필 이미지 업로드 URL 발급")
    void testIssueStorageUrl_givenImagePathAndContentType_willReturnStorageUrl() {
        // given
        given(amazonS3Service.generatePutPresignedUrl(any(), any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_STORAGE_URL);
        willDoNothing().given(pendingFileService).trackPendingFiles(any());

        // when
        String storageUrl = memberImageIOHelper.issueStorageUrl(testMemberProfileImagePath, MEMBER_PROFILE_BASIC_USER_IMAGE_CONTENT_TYPE);

        // then
        assertThat(storageUrl).isEqualTo(MEMBER_PROFILE_BASIC_USER_IMAGE_STORAGE_URL);
        verify(pendingFileService, times(1)).trackPendingFiles(List.of(testMemberProfileImagePath.getValue()));
    }

    @Test
    @DisplayName("uploadImage를 통해 보고서 이미지 업로드")
    void testUploadImage_givenReportImage_willReturnImagePath() throws IOException {
        // given
        willDoNothing().given(amazonS3Service).uploadFile(any(), any());

        // when
        List<String> imagePaths = memberImageIOHelper.uploadImage(
                testMemberId, testReportId, TEST_REPORT_IMAGES);

        // then
        assertThat(imagePaths).isEqualTo(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATHS);
    }

    @Test
    @DisplayName("issueStorageUrl을 통해 보고서 이미지 업로드 URL 발급")
    void testIssueStorageUrl_givenReportImagePathAndContentType_willReturnStorageUrl() {
        // given
        given(amazonS3Service.generatePutPresignedUrl(any(), any())).willReturn(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_STORAGE_URL_1);
        willDoNothing().given(pendingFileService).trackPendingFiles(any());

        // when
        String storageUrl = memberImageIOHelper.issueStorageUrl(testReportImagePath1, TEST_REPORT_IMAGE_CONTENT_TYPE);

        // then
        assertThat(storageUrl).isEqualTo(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_STORAGE_URL_1);
        verify(pendingFileService, times(1)).trackPendingFiles(List.of(testReportImagePath1.getValue()));
    }

    @Test
    @DisplayName("존재하는 이미지 경로로 validateIfImageExistsInStorage 검증 활동 수행")
    void testValidateIfImageExistsInStorage_givenExistingImagePath_willProcessAction() {
        // given
        given(amazonS3Service.checkIfFileExists(testMemberProfileImagePath.getValue())).willReturn(true);

        // when
        memberImageIOHelper.validateIfImageExistsInStorage(testMemberProfileImagePath);

        // then
        verify(amazonS3Service, times(1)).checkIfFileExists(testMemberProfileImagePath.getValue());
    }

    @Test
    @DisplayName("존재하지 않는 이미지 경로로 validateIfImageExistsInStorage 검증 예외 반환")
    void testValidateIfImageExistsInStorage_givenNonExistingImagePath_willThrowException() {
        // given
        given(amazonS3Service.checkIfFileExists(testMemberProfileImagePath.getValue())).willReturn(false);

        // when & then
        NotFoundFileKeyOnS3Exception notFoundFileKeyOnS3Exception = assertThrows(
                NotFoundFileKeyOnS3Exception.class,
                () -> memberImageIOHelper.validateIfImageExistsInStorage(testMemberProfileImagePath));
        assertThat(notFoundFileKeyOnS3Exception.getErrorCode()).isEqualTo(AWSErrorCode.NOT_FOUND_FILE_KEY_ON_S3);
    }

    @Test
    @DisplayName("null 이미지 경로로 validateIfImageExistsInStorage 검증 활동 수행")
    void testValidateIfImageExistsInStorage_givenNullImagePath_willProcessAction() {
        // given & when
        memberImageIOHelper.validateIfImageExistsInStorage(testEmptyMemberProfileImagePath);

        // then
        verify(amazonS3Service, never()).checkIfFileExists(any());
    }

    @Test
    @DisplayName("null인 회원 프로필 이미지 경로로 issueStorageUrl을 통해 null 반환")
    void testIssueStorageUrl_givenNullMemberProfileImagePath_willReturnNull() {
        // when
        String storageUrl = memberImageIOHelper.issueStorageUrl(testEmptyMemberProfileImagePath, MEMBER_PROFILE_BASIC_USER_IMAGE_CONTENT_TYPE);

        // then
        assertThat(storageUrl).isNull();
        verify(amazonS3Service, never()).generatePutPresignedUrl(any(), any());
        verify(pendingFileService, never()).trackPendingFiles(any());
    }

    @Test
    @DisplayName("null인 보고서 이미지 경로로 issueStorageUrl을 통해 null 반환")
    void testIssueStorageUrl_givenNullReportImagePath_willReturnNull() {
        // when
        String storageUrl = memberImageIOHelper.issueStorageUrl(testEmptyReportImagePath, TEST_REPORT_IMAGE_CONTENT_TYPE);

        // then
        assertThat(storageUrl).isNull();
        verify(amazonS3Service, never()).generatePutPresignedUrl(any(), any());
        verify(pendingFileService, never()).trackPendingFiles(any());
    }

    @Test
    @DisplayName("이미지가 없는 데이터로 uploadImage를 통해 null 반환")
    void testUploadImage_givenNullMemberProfileImage_willReturnNull() throws IOException {
        // given
        MemberProfileOverrideRecord_V1 record = new MemberProfileOverrideRecord_V1(
                MEMBER_BASIC_USER_UUID, MEMBER_PROFILE_BASIC_USER_INTRODUCTION, null, MEMBER_BASIC_USER_NICKNAME);

        // when
        String imagePath = memberImageIOHelper.uploadImage(testMemberId, record);

        // then
        assertThat(imagePath).isNull();
        verify(amazonS3Service, never()).uploadFile(any(), any());
    }

    @Test
    @DisplayName("null이 포함된 보고서 이미지 리스트로 uploadImage를 통해 null을 제외한 이미지 경로 반환")
    void testUploadImage_givenReportImagesContainingNull_willReturnImagePathsWithoutNull() throws IOException {
        // when
        List<String> imagePaths = memberImageIOHelper.uploadImage(
                testMemberId, testReportId, TEST_REPORT_IMAGES_WITH_NULL);

        // then
        assertThat(imagePaths).containsExactly(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1);
        verify(amazonS3Service, times(1)).uploadFile(any(), any());
    }
}