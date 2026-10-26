package kr.modusplant.domains.post.adapter.listener;

import kr.modusplant.domains.member.domain.event.PostAbuseReportApproveEvent;
import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.exception.PostNotFoundException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.domains.post.domain.vo.PostAuthorId;
import kr.modusplant.domains.post.usecase.port.processor.ContentDataProcessorPort;
import kr.modusplant.domains.post.usecase.port.repository.PostArchiveRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostAuthorRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostRepository;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_ADMIN_UUID;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class PostAdminEventListenerTest implements PostTestUtils {
    private final PostRepository postRepository = Mockito.mock(PostRepository.class);
    private final PostArchiveRepository postArchiveRepository = Mockito.mock(PostArchiveRepository.class);
    private final PostAuthorRepository postAuthorRepository = Mockito.mock(PostAuthorRepository.class);
    private final ContentDataProcessorPort contentDataProcessorPort = Mockito.mock(ContentDataProcessorPort.class);
    private final PostAdminEventListener postAdminEventListener = new PostAdminEventListener(
            postRepository, postArchiveRepository, postAuthorRepository, contentDataProcessorPort);

    @Test
    @DisplayName("관리자의 게시글 신고 승인 이벤트로 발행된 게시글 삭제 시 활동 수행")
    void testDeletePost_givenAdminMemberAndPublishedPost_willDeletePost() {
        // given
        PostAbuseReportApproveEvent event = PostAbuseReportApproveEvent.create(TEST_POST_ULID, MEMBER_BASIC_ADMIN_UUID);
        Post post = createPublishedPost();
        given(postAuthorRepository.getRole(PostAuthorId.fromUuid(MEMBER_BASIC_ADMIN_UUID))).willReturn(Role.ADMIN);
        given(postRepository.getPostByUlid(testPostId)).willReturn(Optional.of(post));

        // when
        postAdminEventListener.deletePost(event);

        // then
        verify(postArchiveRepository).save(testPostId);
        verify(postRepository).deletePostLikeByPostId(testPostId);
        verify(postRepository).deletePostBookmarkByPostId(testPostId);
        verify(postRepository).deletePostAbuseReportByPostId(testPostId);
        verify(postRepository).deletePostAbuseReportDashboardByPostId(testPostId);
        verify(postRepository).deletePostRecentlyViewRecordByPostId(testPostId);
        verify(contentDataProcessorPort).deleteFiles(post.getPostContent().getContent());
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("관리자의 게시글 신고 승인 이벤트로 임시 저장 게시글 삭제 시 활동 수행")
    void testDeletePost_givenAdminMemberAndDraftPost_willDeletePost() {
        // given
        PostAbuseReportApproveEvent event = PostAbuseReportApproveEvent.create(TEST_POST_ULID, MEMBER_BASIC_ADMIN_UUID);
        Post post = createDraftPost();
        given(postAuthorRepository.getRole(PostAuthorId.fromUuid(MEMBER_BASIC_ADMIN_UUID))).willReturn(Role.ADMIN);
        given(postRepository.getPostByUlid(testPostId)).willReturn(Optional.of(post));

        // when
        postAdminEventListener.deletePost(event);

        // then
        verify(postArchiveRepository, never()).save(any());
        verify(postRepository, never()).deletePostAbuseReportByPostId(any());
        verify(postRepository, never()).deletePostAbuseReportDashboardByPostId(any());
        verify(contentDataProcessorPort).deleteFiles(post.getPostContent().getContent());
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("관리자의 게시글 신고 승인 이벤트로 존재하지 않는 게시글 삭제 시 예외 반환")
    void testDeletePost_givenNonExistentPost_willThrowException() {
        // given
        PostAbuseReportApproveEvent event = PostAbuseReportApproveEvent.create(TEST_POST_ULID, MEMBER_BASIC_ADMIN_UUID);
        given(postAuthorRepository.getRole(PostAuthorId.fromUuid(MEMBER_BASIC_ADMIN_UUID))).willReturn(Role.ADMIN);
        given(postRepository.getPostByUlid(testPostId)).willReturn(Optional.empty());

        // when
        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> postAdminEventListener.deletePost(event));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorCode.POST_NOT_FOUND);
        verify(postRepository, never()).delete(any());
    }

    @Test
    @DisplayName("관리자가 아닌 회원의 게시글 신고 승인 이벤트로 게시글 삭제 시 예외 반환")
    void testDeletePost_givenNonAdminMember_willThrowException() {
        // given
        PostAbuseReportApproveEvent event = PostAbuseReportApproveEvent.create(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);
        given(postAuthorRepository.getRole(PostAuthorId.fromUuid(MEMBER_BASIC_USER_UUID))).willReturn(Role.USER);

        // when
        NotAccessibleException exception = assertThrows(NotAccessibleException.class,
                () -> postAdminEventListener.deletePost(event));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.ADMIN_ROLE_REQUIRED_TO_ACCESS);
        verify(postRepository, never()).getPostByUlid(any());
    }
}
