package kr.modusplant.infrastructure.event.listener;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.entity.CommentLikeEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.CommentLikeJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static kr.modusplant.shared.event.common.util.CommentLikeEventTestUtils.testCommentLikeEvent;
import static kr.modusplant.shared.event.common.util.CommentUnlikeEventTestUtils.testCommentUnlikeEvent;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class CommentEventListenerTest {
    private final CommentLikeJpaRepository commCommentLikeRepository = mock(CommentLikeJpaRepository.class);
    private final CommentJpaRepository commCommentRepository = mock(CommentJpaRepository.class);
    private final CommentEventListener commentEventListener = new CommentEventListener(commCommentLikeRepository, commCommentRepository);

    @Test
    @DisplayName("존재하는 댓글에 대해 handleCommentLikeEvent 실행 시 좋아요 저장 및 카운트 증가")
    void testHandleCommentLikeEvent_givenExistedComment_willIncreaseLikeCount() {
        // given
        CommentEntity mockComment = mock(CommentEntity.class);
        given(commCommentRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.of(mockComment));

        // when
        commentEventListener.handleCommentLikeEvent(testCommentLikeEvent);

        // then
        verify(commCommentLikeRepository, times(1)).save(any(CommentLikeEntity.class));
        verify(mockComment, times(1)).increaseLikeCount();
    }

    @Test
    @DisplayName("존재하지 않는 댓글로 인해 handleCommentLikeEvent 실행 실패")
    void testHandleCommentLikeEvent_givenNotFoundComment_willThrowException() {
        // given
        given(commCommentRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> commentEventListener.handleCommentLikeEvent(testCommentLikeEvent));

        // then
        verify(commCommentLikeRepository, times(1)).save(any(CommentLikeEntity.class));
    }

    @Test
    @DisplayName("존재하는 댓글에 대해 handleCommentUnlikeEvent 실행 시 좋아요 삭제 및 카운트 감소")
    void testHandleCommentUnlikeEvent_givenExistedComment_willDecreaseLikeCount() {
        // given
        CommentEntity mockComment = mock(CommentEntity.class);
        given(commCommentRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.of(mockComment));

        // when
        commentEventListener.handleCommentUnlikeEvent(testCommentUnlikeEvent);

        // then
        verify(commCommentLikeRepository, times(1)).delete(any(CommentLikeEntity.class));
        verify(mockComment, times(1)).decreaseLikeCount();
    }

    @Test
    @DisplayName("존재하지 않는 댓글로 인해 handleCommentUnlikeEvent 실행 실패")
    void testHandleCommentUnlikeEvent_givenNotFoundComment_willThrowException() {
        // given
        given(commCommentRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> commentEventListener.handleCommentUnlikeEvent(testCommentUnlikeEvent));

        // then
        verify(commCommentLikeRepository, times(1)).delete(any(CommentLikeEntity.class));
    }
}