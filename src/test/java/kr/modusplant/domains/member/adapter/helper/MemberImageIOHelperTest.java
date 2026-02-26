package kr.modusplant.domains.member.adapter.helper;

import kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.member.common.util.usecase.record.MemberProfileOverrideRecordTestUtils;
import kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRecordTestUtils;
import kr.modusplant.framework.aws.service.S3FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.REPORT_IMAGE_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;

class MemberImageIOHelperTest implements MemberIdTestUtils, MemberProfileOverrideRecordTestUtils, ProposalOrBugReportRecordTestUtils {
    private final S3FileService s3FileService = Mockito.mock(S3FileService.class);
    private final MemberImageIOHelper memberImageIOHelper = new MemberImageIOHelper(s3FileService);

    @Test
    @DisplayName("uploadImage를 통해 회원 프로필 이미지 업로드")
    void testUploadImage_givenMemberProfileImage_willReturnImagePath() throws IOException {
        // given
        willDoNothing().given(s3FileService).uploadFile(any(), any());

        // when
        String imagePath = memberImageIOHelper.uploadImage(testMemberId, testMemberProfileOverrideRecord);

        // then
        assertThat(imagePath).isEqualTo(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH);
    }

    @Test
    @DisplayName("uploadImage를 통해 보고서 이미지 업로드")
    void testUploadImage_givenReportImage_willReturnImagePath() throws IOException {
        // given
        willDoNothing().given(s3FileService).uploadFile(any(), any());

        // when
        String imagePath = memberImageIOHelper.uploadImage(testMemberId, testProposalOrBugReportRecord);

        // then
        assertThat(imagePath).isEqualTo(REPORT_IMAGE_PATH);
    }
}