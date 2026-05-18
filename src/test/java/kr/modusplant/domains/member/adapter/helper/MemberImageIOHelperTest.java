package kr.modusplant.domains.member.adapter.helper;

import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileOverrideRecordTestUtils.testMemberProfileOverrideRecord;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_IMAGES;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATHS;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;

class MemberImageIOHelperTest {
    private final AmazonS3Service amazonS3Service = Mockito.mock(AmazonS3Service.class);
    private final MemberImageIOHelper memberImageIOHelper = new MemberImageIOHelper(amazonS3Service);

    @Test
    @DisplayName("uploadImage를 통해 회원 프로필 이미지 업로드")
    void testUploadImage_givenMemberProfileImage_willReturnImagePath() throws IOException {
        // given
        willDoNothing().given(amazonS3Service).uploadFile(any(), any());

        // when
        String imagePath = memberImageIOHelper.uploadImage(testMemberId, testMemberProfileOverrideRecord);

        // then
        assertThat(imagePath).isEqualTo(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH);
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
}