package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.entity.CommentLikeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetCommentIdTestUtils.testTargetCommentId;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TargetCommentRepositoryJpaAdapterTest {
    CommentJpaRepository commentJpaRepository = Mockito.mock(CommentJpaRepository.class);
    CommentLikeJpaRepository commentLikeJpaRepository = Mockito.mock(CommentLikeJpaRepository.class);
    TargetCommentRepositoryJpaAdapter targetCommentRepositoryJpaAdapter = new TargetCommentRepositoryJpaAdapter(commentJpaRepository, commentLikeJpaRepository);

    @Test
    @DisplayName("isIdExist로 true 반환")
    void testIsIdExist_givenIdThatExists_willReturnTrue() {
        // given & when
        given(commentJpaRepository.existsByPostUlidAndPath(TEST_POST_ULID, TEST_COMM_COMMENT_PATH)).willReturn(true);

        // when & then
        assertThat(targetCommentRepositoryJpaAdapter.isIdExist(testTargetCommentId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isIdExist로 false 반환")
    void testIsIdExist_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(commentJpaRepository.existsByPostUlidAndPath(TEST_POST_ULID, TEST_COMM_COMMENT_PATH)).willReturn(false);

        // when & then
        assertThat(targetCommentRepositoryJpaAdapter.isIdExist(testTargetCommentId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isLiked로 true 반환")
    void testIsLiked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(commentLikeJpaRepository.existsByPostIdAndPathAndMemberId(TEST_POST_ULID, TEST_COMM_COMMENT_PATH, testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(targetCommentRepositoryJpaAdapter.isLiked(testMemberId, testTargetCommentId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isLiked로 false 반환")
    void testIsLiked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(commentLikeJpaRepository.existsByPostIdAndPathAndMemberId(TEST_POST_ULID, TEST_COMM_COMMENT_PATH, testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(targetCommentRepositoryJpaAdapter.isLiked(testMemberId, testTargetCommentId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 true 반환")
    void testIsUnliked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(commentLikeJpaRepository.existsByPostIdAndPathAndMemberId(TEST_POST_ULID, TEST_COMM_COMMENT_PATH, testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(targetCommentRepositoryJpaAdapter.isUnliked(testMemberId, testTargetCommentId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isUnliked로 false 반환")
    void testIsUnliked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(commentLikeJpaRepository.existsByPostIdAndPathAndMemberId(TEST_POST_ULID, TEST_COMM_COMMENT_PATH, testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(targetCommentRepositoryJpaAdapter.isUnliked(testMemberId, testTargetCommentId)).isEqualTo(false);
    }

    @Test
    @DisplayName("존재하는 댓글에 대해 like 실행 시 좋아요 저장 및 카운트 증가")
    void testLike_givenExistedComment_willIncreaseLikeCount() {
        // given
        CommentEntity mockComment = mock(CommentEntity.class);
        given(commentJpaRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.of(mockComment));

        // when
        targetCommentRepositoryJpaAdapter.like(testMemberId, testTargetCommentId);

        // then
        verify(commentLikeJpaRepository, times(1)).save(any(CommentLikeEntity.class));
        verify(mockComment, times(1)).increaseLikeCount();
    }

    @Test
    @DisplayName("존재하지 않는 댓글로 인해 like 실행 실패")
    void testLike_givenNotFoundComment_willThrowException() {
        // given
        given(commentJpaRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> targetCommentRepositoryJpaAdapter.like(testMemberId, testTargetCommentId));

        // then
        verify(commentLikeJpaRepository, times(1)).save(any(CommentLikeEntity.class));
    }

    @Test
    @DisplayName("존재하는 댓글에 대해 unlike 실행 시 좋아요 삭제 및 카운트 감소")
    void testUnlike_givenExistedComment_willDecreaseLikeCount() {
        // given
        CommentEntity mockComment = mock(CommentEntity.class);
        given(commentJpaRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.of(mockComment));

        // when
        targetCommentRepositoryJpaAdapter.unlike(testMemberId, testTargetCommentId);

        // then
        verify(commentLikeJpaRepository, times(1)).delete(any(CommentLikeEntity.class));
        verify(mockComment, times(1)).decreaseLikeCount();
    }

    @Test
    @DisplayName("존재하지 않는 댓글로 인해 handleCommentUnlikeEvent 실행 실패")
    void testHandleCommentUnlikeEvent_givenNotFoundComment_willThrowException() {
        // given
        given(commentJpaRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> targetCommentRepositoryJpaAdapter.unlike(testMemberId, testTargetCommentId));

        // then
        verify(commentLikeJpaRepository, times(1)).delete(any(CommentLikeEntity.class));
    }
}