package kr.modusplant.domains.comment.adapter.listener;

import kr.modusplant.domains.comment.adapter.helper.CommentValidationHelper;
import kr.modusplant.domains.comment.common.util.domain.vo.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.ParentPostIdTestUtils;
import kr.modusplant.domains.comment.domain.vo.CommentAuthorId;
import kr.modusplant.domains.comment.usecase.port.repository.CommentAuthorRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentCommandRepository;
import kr.modusplant.domains.member.domain.event.CommentAbuseReportApproveEvent;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_ADMIN_UUID;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CommentAdminEventListenerTest implements ParentPostIdTestUtils, CommentPathTestUtils {
    private final CommentCommandRepository commandRepository = Mockito.mock(CommentCommandRepository.class);
    private final CommentAuthorRepository authorRepository = Mockito.mock(CommentAuthorRepository.class);
    private final CommentValidationHelper commentValidationHelper = Mockito.mock(CommentValidationHelper.class);
    private final CommentAdminEventListener commentAdminEventListener = new CommentAdminEventListener(
            commandRepository, authorRepository, commentValidationHelper);

    @Test
    @DisplayName("관리자의 댓글 신고 승인 이벤트로 댓글 삭제 시 활동 수행")
    void testDeleteComment_givenAdminMember_willDeleteComment() {
        // given
        CommentAbuseReportApproveEvent event =
                CommentAbuseReportApproveEvent.create(TEST_POST_ULID, TEST_COMMENT_PATH, MEMBER_BASIC_ADMIN_UUID);
        given(authorRepository.getRole(CommentAuthorId.fromUuid(MEMBER_BASIC_ADMIN_UUID))).willReturn(Role.ADMIN);

        // when
        commentAdminEventListener.deleteComment(event);

        // then
        verify(commentValidationHelper).validateIfParentPostExists(testParentPostId);
        verify(commentValidationHelper).validateIfParentPostIsPublished(testParentPostId);
        verify(commentValidationHelper).validateIfCommentExists(testParentPostId, testCommentPath);
        verify(commandRepository).deleteComment(testParentPostId, testCommentPath);
    }

    @Test
    @DisplayName("관리자가 아닌 회원의 댓글 신고 승인 이벤트로 댓글 삭제 시 예외 반환")
    void testDeleteComment_givenNonAdminMember_willThrowException() {
        // given
        CommentAbuseReportApproveEvent event =
                CommentAbuseReportApproveEvent.create(TEST_POST_ULID, TEST_COMMENT_PATH, MEMBER_BASIC_USER_UUID);
        given(authorRepository.getRole(CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID))).willReturn(Role.USER);

        // when
        NotAccessibleException exception = assertThrows(NotAccessibleException.class,
                () -> commentAdminEventListener.deleteComment(event));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.ADMIN_ROLE_REQUIRED_TO_ACCESS);
        verify(commandRepository, never()).deleteComment(any(), any());
    }
}
