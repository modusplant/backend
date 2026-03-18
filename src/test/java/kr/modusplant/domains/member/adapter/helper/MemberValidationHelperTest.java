package kr.modusplant.domains.member.adapter.helper;

import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetCommentIdRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetPostIdRepository;
import kr.modusplant.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetCommentIdTestUtils.testTargetCommentId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetPostIdTestUtils.testTargetPostId;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

class MemberValidationHelperTest {
    private final MemberRepository memberRepository = Mockito.mock(MemberRepository.class);
    private final TargetPostIdRepository targetPostIdRepository = Mockito.mock(TargetPostIdRepository.class);
    private final TargetCommentIdRepository targetCommentIdRepository = Mockito.mock(TargetCommentIdRepository.class);
    private final MemberValidationHelper memberValidationHelper = new MemberValidationHelper(memberRepository, targetPostIdRepository, targetCommentIdRepository);

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
            assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
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
    class ValidateIfTargetPostExistsTest {
        @Test
        @DisplayName("타겟 게시글이 존재할 때 타겟 게시글의 존재 여부 검증")
        void testValidateIfTargetPostExists_givenExistedTargetPost_willReturnNothing() {
            // given & when
            given(targetPostIdRepository.isIdExist(testTargetPostId)).willReturn(true);

            // then
            assertDoesNotThrow(() -> memberValidationHelper.validateIfTargetPostExists(testTargetPostId));
        }

        @Test
        @DisplayName("타겟 게시글이 존재하지 않을 때 타겟 게시글의 존재 여부 검증")
        void testValidateIfTargetPostExists_givenNotFoundTargetPost_willThrowException() {
            // given
            given(targetPostIdRepository.isIdExist(testTargetPostId)).willReturn(false);

            // when
            NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class, () -> memberValidationHelper.validateIfTargetPostExists(testTargetPostId));

            // then
            assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_POST_ID);
        }
    }

    @Nested
    @DisplayName("타겟 댓글의 존재 여부 검증")
    class ValidateIfTargetCommentExistsTest {
        @Test
        @DisplayName("타겟 댓글이 존재할 때 타겟 댓글의 존재 여부 검증")
        void testValidateIfTargetCommentExists_givenExistedComment_willReturnNothing() {
            // given & when
            given(targetCommentIdRepository.isIdExist(testTargetCommentId)).willReturn(true);

            // then
            assertDoesNotThrow(() -> memberValidationHelper.validateIfTargetCommentExists(testTargetCommentId));
        }

        @Test
        @DisplayName("타겟 댓글이 존재하지 않을 때 타겟 댓글의 존재 여부 검증")
        void testValidateIfTargetCommentExists_givenNotFoundComment_willThrowException() {
            // given
            given(targetCommentIdRepository.isIdExist(testTargetCommentId)).willReturn(false);

            // when
            NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class, () -> memberValidationHelper.validateIfTargetCommentExists(testTargetCommentId));

            // then
            assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_COMMENT_ID);
        }
    }
}