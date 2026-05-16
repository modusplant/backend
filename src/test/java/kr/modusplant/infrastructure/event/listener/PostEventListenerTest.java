package kr.modusplant.infrastructure.event.listener;

import kr.modusplant.framework.jpa.entity.PostBookmarkEntity;
import kr.modusplant.framework.jpa.entity.PostEntity;
import kr.modusplant.framework.jpa.entity.PostLikeEntity;
import kr.modusplant.framework.jpa.repository.PostBookmarkJpaRepository;
import kr.modusplant.framework.jpa.repository.PostJpaRepository;
import kr.modusplant.framework.jpa.repository.PostLikeJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static kr.modusplant.shared.event.common.util.PostBookmarkEventTestUtils.testPostBookmarkEvent;
import static kr.modusplant.shared.event.common.util.PostCancelPostBookmarkEventTestUtils.testPostBookmarkCancelEvent;
import static kr.modusplant.shared.event.common.util.PostLikeEventTestUtils.testPostLikeEvent;
import static kr.modusplant.shared.event.common.util.PostUnlikeEventTestUtils.testPostUnlikeEvent;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class PostEventListenerTest {
    private final PostLikeJpaRepository postLikeRepository = mock(PostLikeJpaRepository.class);
    private final PostBookmarkJpaRepository postBookmarkRepository = mock(PostBookmarkJpaRepository.class);
    private final PostJpaRepository postRepository = mock(PostJpaRepository.class);
    private final PostEventListener postEventListener = new PostEventListener(
            postLikeRepository, postBookmarkRepository, postRepository
    );

    @Test
    @DisplayName("존재하는 게시글에 대해 handlePostLikeEvent 실행 시 좋아요 저장 및 카운트 증가")
    void testHandlePostLikeEvent_givenExistedPost_willIncreaseLikeCount() {
        // given
        PostEntity mockPost = mock(PostEntity.class);
        given(postRepository.findByUlid(any())).willReturn(Optional.of(mockPost));

        // when
        postEventListener.handlePostLikeEvent(testPostLikeEvent);

        // then
        verify(postLikeRepository, times(1)).save(any(PostLikeEntity.class));
        verify(mockPost, times(1)).increaseLikeCount();
    }

    @Test
    @DisplayName("존재하지 않는 게시글로 인해 handlePostLikeEvent 실행 실패")
    void testHandlePostLikeEvent_givenNotFoundPost_willThrowException() {
        // given
        given(postRepository.findByUlid(any())).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> postEventListener.handlePostLikeEvent(testPostLikeEvent));

        // then
        verify(postLikeRepository, times(1)).save(any(PostLikeEntity.class));
    }

    @Test
    @DisplayName("존재하는 게시글에 대해 handlePostUnlikeEvent 실행 시 좋아요 삭제 및 카운트 감소")
    void testHandlePostUnlikeEvent_givenExistedPost_willDecreaseLikeCount() {
        // given
        PostEntity mockPost = mock(PostEntity.class);
        given(postRepository.findByUlid(any())).willReturn(Optional.of(mockPost));

        // when
        postEventListener.handlePostUnlikeEvent(testPostUnlikeEvent);

        // then
        verify(postLikeRepository, times(1)).delete(any(PostLikeEntity.class));
        verify(mockPost, times(1)).decreaseLikeCount();
    }

    @Test
    @DisplayName("존재하지 않는 게시글로 인해 handlePostUnlikeEvent 실행 실패")
    void testHandlePostUnlikeEvent_givenNotFoundPost_willThrowException() {
        // given
        given(postRepository.findByUlid(any())).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> postEventListener.handlePostUnlikeEvent(testPostUnlikeEvent));

        // then
        verify(postLikeRepository, times(1)).delete(any(PostLikeEntity.class));
    }

    @Test
    @DisplayName("handlePostBookmarkEvent 실행 시 게시글 북마크 정상 저장")
    void testHandlePostBookmarkEvent_givenValidData_willSaveBookmark() {
        // given & when
        postEventListener.handlePostBookmarkEvent(testPostBookmarkEvent);

        // then
        verify(postBookmarkRepository, times(1)).save(any(PostBookmarkEntity.class));
    }

    @Test
    @DisplayName("handlePostBookmarkCancelEvent 실행 시 게시글 북마크 정상 삭제")
    void testHandlePostBookmarkCancelEvent_givenValidData_willDeleteBookmark() {
        // given & when
        postEventListener.handlePostBookmarkCancelEvent(testPostBookmarkCancelEvent);

        // then
        verify(postBookmarkRepository, times(1)).delete(any(PostBookmarkEntity.class));
    }
}