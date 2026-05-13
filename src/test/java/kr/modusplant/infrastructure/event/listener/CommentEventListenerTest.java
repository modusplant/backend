package kr.modusplant.infrastructure.event.listener;

import kr.modusplant.framework.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.jpa.entity.CommCommentLikeEntity;
import kr.modusplant.framework.jpa.repository.CommCommentJpaRepository;
import kr.modusplant.framework.jpa.repository.CommCommentLikeJpaRepository;
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
    private final CommCommentLikeJpaRepository commCommentLikeRepository = mock(CommCommentLikeJpaRepository.class);
    private final CommCommentJpaRepository commCommentRepository = mock(CommCommentJpaRepository.class);
    private final CommentEventListener commentEventListener = new CommentEventListener(commCommentLikeRepository, commCommentRepository);

    @Test
    @DisplayName("존재하는 댓글에 대해 handleCommentLikeEvent 실행 시 좋아요 저장 및 카운트 증가")
    void testHandleCommentLikeEvent_givenExistedComment_willIncreaseLikeCount() {
        // given
        CommCommentEntity mockComment = mock(CommCommentEntity.class);
        given(commCommentRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.of(mockComment));

        // when
        commentEventListener.handleCommentLikeEvent(testCommentLikeEvent);

        // then
        verify(commCommentLikeRepository, times(1)).save(any(CommCommentLikeEntity.class));
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
        verify(commCommentLikeRepository, times(1)).save(any(CommCommentLikeEntity.class));
    }

    @Test
    @DisplayName("존재하는 댓글에 대해 handleCommentUnlikeEvent 실행 시 좋아요 삭제 및 카운트 감소")
    void testHandleCommentUnlikeEvent_givenExistedComment_willDecreaseLikeCount() {
        // given
        CommCommentEntity mockComment = mock(CommCommentEntity.class);
        given(commCommentRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.of(mockComment));

        // when
        commentEventListener.handleCommentUnlikeEvent(testCommentUnlikeEvent);

        // then
        verify(commCommentLikeRepository, times(1)).delete(any(CommCommentLikeEntity.class));
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
        verify(commCommentLikeRepository, times(1)).delete(any(CommCommentLikeEntity.class));
    }
}