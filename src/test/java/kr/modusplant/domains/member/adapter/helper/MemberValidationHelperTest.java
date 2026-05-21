package kr.modusplant.domains.member.adapter.helper;

import kr.modusplant.domains.member.usecase.port.repository.*;
import kr.modusplant.shared.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectCommentIdTestUtils.testActivitySubjectCommentId;
import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectPostIdTestUtils.testActivitySubjectPostId;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

class MemberValidationHelperTest {
    private final MemberRepository memberRepository = Mockito.mock(MemberRepository.class);
    private final MemberProfileRepository memberProfileRepository = Mockito.mock(MemberProfileRepository.class);
    private final ReportRepository reportRepository = Mockito.mock(ReportRepository.class);
    private final ActivitySubjectPostRepository activitySubjectPostRepository = Mockito.mock(ActivitySubjectPostRepository.class);
    private final ActivitySubjectCommentRepository activitySubjectCommentRepository = Mockito.mock(ActivitySubjectCommentRepository.class);
    private final MemberValidationHelper memberValidationHelper =
            new MemberValidationHelper(memberRepository, memberProfileRepository, reportRepository, activitySubjectPostRepository, activitySubjectCommentRepository);

    @Nested
    @DisplayName("회원의 존재 여부 검증")
    class ValidateIfMemberExistsTest {
        @Test
        @DisplayName("회원이 존재할 때 회원의 존재 여부 검증")
        void testValidateIfMemberExists_givenExistedMember_willReturnNothing() {
            // given & when
            given(memberRepository.isIdExist(testMemberId)).willReturn(true);

            // then
            assertDoesNotThrow(() -> memberValidationHelper.validateIfMemberExists(testMemberId));
        }

        @Test
        @DisplayName("회원이 존재하지 않을 때 회원의 존재 여부 검증")
        void testValidateIfMemberExists_givenNotFoundMember_willThrowException() {
            // given
            given(memberRepository.isIdExist(testMemberId)).willReturn(false);

            // when
            NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class, () -> memberValidationHelper.validateIfMemberExists(testMemberId));
            
            // then
            assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
        }
    }

    @Nested
    @DisplayName("회원 프로필의 존재 여부 검증")
    class ValidateIfMemberProfileExistsTest {
        @Test
        @DisplayName("회원 프로필이 존재할 때 회원 프로필의 존재 여부 검증")
        void testValidateIfMemberProfileExists_givenExistedProfile_willReturnNothing() {
            // given & when
            given(memberProfileRepository.isIdExist(testMemberId)).willReturn(true);

            // then
            assertDoesNotThrow(() -> memberValidationHelper.validateIfMemberProfileExists(testMemberId));
        }

        @Test
        @DisplayName("회원 프로필이 존재하지 않을 때 회원 프로필의 존재 여부 검증")
        void testValidateIfMemberProfileExists_givenNotFoundProfile_willThrowException() {
            // given
            given(memberProfileRepository.isIdExist(testMemberId)).willReturn(false);

            // when
            NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class, () -> memberValidationHelper.validateIfMemberProfileExists(testMemberId));

            // then
            assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_PROFILE);
        }
    }

    @Nested
    @DisplayName("닉네임의 중복 여부 검증")
    class ValidateIfNicknameNotDuplicatedTest {
        @Test
        @DisplayName("닉네임이 이미 존재할 때 닉네임의 중복 여부 검증")
        void testValidateIfNicknameNotDuplicated_givenExistedNickname_willThrowException() {
            // given
            given(memberRepository.isNicknameExist(testNormalUserNickname)).willReturn(true);

            // when
            ExistsEntityException existsEntityException = assertThrows(ExistsEntityException.class, () -> memberValidationHelper.validateIfNicknameNotDuplicated(testNormalUserNickname));

            // then
            assertThat(existsEntityException.getErrorCode()).isEqualTo(KernelErrorCode.EXISTS_NICKNAME);
        }

        @Test
        @DisplayName("닉네임이 존재하지 않을 때 닉네임의 중복 여부 검증")
        void testValidateIfNicknameNotDuplicated_givenNotFoundNickname_willReturnNothing() {
            // given & when
            given(memberRepository.isNicknameExist(testNormalUserNickname)).willReturn(false);

            // then
            assertDoesNotThrow(() -> memberValidationHelper.validateIfNicknameNotDuplicated(testNormalUserNickname));
        }
    }

    @Nested
    @DisplayName("타겟 게시글의 존재 여부 검증")
    class ValidateIfActivitySubjectPostExistsTest {
        @Test
        @DisplayName("타겟 게시글이 존재할 때 타겟 게시글의 존재 여부 검증")
        void testValidateIfActivitySubjectPostExists_givenExistedActivitySubjectPost_willReturnNothing() {
            // given & when
            given(activitySubjectPostRepository.isIdExist(testActivitySubjectPostId)).willReturn(true);

            // then
            assertDoesNotThrow(() -> memberValidationHelper.validateIfActivitySubjectPostExists(testActivitySubjectPostId));
        }

        @Test
        @DisplayName("타겟 게시글이 존재하지 않을 때 타겟 게시글의 존재 여부 검증")
        void testValidateIfActivitySubjectPostExists_givenNotFoundActivitySubjectPost_willThrowException() {
            // given
            given(activitySubjectPostRepository.isIdExist(testActivitySubjectPostId)).willReturn(false);

            // when
            NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class, () -> memberValidationHelper.validateIfActivitySubjectPostExists(testActivitySubjectPostId));

            // then
            assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID);
        }
    }

    @Nested
    @DisplayName("타겟 댓글의 존재 여부 검증")
    class ValidateIfActivitySubjectCommentExistsTest {
        @Test
        @DisplayName("타겟 댓글이 존재할 때 타겟 댓글의 존재 여부 검증")
        void testValidateIfActivitySubjectCommentExists_givenExistedComment_willReturnNothing() {
            // given & when
            given(activitySubjectCommentRepository.isIdExist(testActivitySubjectCommentId)).willReturn(true);

            // then
            assertDoesNotThrow(() -> memberValidationHelper.validateIfActivitySubjectCommentExists(testActivitySubjectCommentId));
        }

        @Test
        @DisplayName("타겟 댓글이 존재하지 않을 때 타겟 댓글의 존재 여부 검증")
        void testValidateIfActivitySubjectCommentExists_givenNotFoundComment_willThrowException() {
            // given
            given(activitySubjectCommentRepository.isIdExist(testActivitySubjectCommentId)).willReturn(false);

            // when
            NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class, () -> memberValidationHelper.validateIfActivitySubjectCommentExists(testActivitySubjectCommentId));

            // then
            assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID);
        }
    }

    @Nested
    @DisplayName("보고서의 존재 여부 검증")
    class ValidateIfReportExistsTest {
        @Test
        @DisplayName("보고서가 존재할 때 보고서의 존재 여부 검증")
        void testValidateIfReportExists_givenExistedReport_willReturnNothing() {
            // given & when
            given(reportRepository.isIdExist(testReportId)).willReturn(true);

            // then
            assertDoesNotThrow(() -> memberValidationHelper.validateIfReportExists(testReportId));
        }

        @Test
        @DisplayName("타겟 댓글이 존재하지 않을 때 타겟 댓글의 존재 여부 검증")
        void testValidateIfReportExists_givenNotFoundReport_willThrowException() {
            // given
            given(reportRepository.isIdExist(testReportId)).willReturn(false);

            // when
            NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                    () -> memberValidationHelper.validateIfReportExists(testReportId));

            // then
            assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_REPORT_ID);
        }
    }
}