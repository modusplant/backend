package kr.modusplant.domains.member.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostBookmarkEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostLikeEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.common.util.PostEntityTestUtils;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;

import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectPostIdTestUtils.testActivitySubjectPostId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ActivitySubjectPostRepositoryJpaAdapterTest implements PostEntityTestUtils {
    PostJpaRepository postJpaRepository = Mockito.mock(PostJpaRepository.class);
    PostLikeJpaRepository postLikeJpaRepository = Mockito.mock(PostLikeJpaRepository.class);
    PostBookmarkJpaRepository postBookmarkJpaRepository = Mockito.mock(PostBookmarkJpaRepository.class);
    ActivitySubjectPostRepositoryJpaAdapter activitySubjectPostIdRepositoryJpaAdapter = new ActivitySubjectPostRepositoryJpaAdapter(postJpaRepository, postLikeJpaRepository, postBookmarkJpaRepository);

    @Test
    @DisplayName("isIdExist로 true 반환")
    void testIsIdExist_givenIdThatExists_willReturnTrue() {
        // given & when
        given(postJpaRepository.existsByUlid(testActivitySubjectPostId.getValue())).willReturn(true);

        // when & then
        assertThat(activitySubjectPostIdRepositoryJpaAdapter.isIdExist(testActivitySubjectPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isIdExist로 false 반환")
    void testIsIdExist_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(postJpaRepository.existsByUlid(testActivitySubjectPostId.getValue())).willReturn(false);

        // when & then
        assertThat(activitySubjectPostIdRepositoryJpaAdapter.isIdExist(testActivitySubjectPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isPublished로 true 반환")
    void testIsPublished_givenIdThatIsPublished_willReturnTrue() {
        // given & when
        given(postJpaRepository.findByUlid(testActivitySubjectPostId.getValue())).willReturn(Optional.of(createPostEntityBuilder().build()));

        // when & then
        assertThat(activitySubjectPostIdRepositoryJpaAdapter.isPublished(testActivitySubjectPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isPublished로 false 반환")
    void testIsPublished_givenIdThatIsNotPublished_willReturnFalse() {
        // given & when
        given(postJpaRepository.findByUlid(testActivitySubjectPostId.getValue())).willReturn(Optional.of(createPostEntityBuilder().isPublished(false).build()));

        // when & then
        assertThat(activitySubjectPostIdRepositoryJpaAdapter.isPublished(testActivitySubjectPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isLiked로 true 반환")
    void testIsLiked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(postLikeJpaRepository.existsByPostIdAndMemberId(testActivitySubjectPostId.getValue(), testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(activitySubjectPostIdRepositoryJpaAdapter.isLiked(testMemberId, testActivitySubjectPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isLiked로 false 반환")
    void testIsLiked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(postLikeJpaRepository.existsByPostIdAndMemberId(testActivitySubjectPostId.getValue(), testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(activitySubjectPostIdRepositoryJpaAdapter.isLiked(testMemberId, testActivitySubjectPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 true 반환")
    void testIsUnliked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(postLikeJpaRepository.existsByPostIdAndMemberId(testActivitySubjectPostId.getValue(), testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(activitySubjectPostIdRepositoryJpaAdapter.isUnliked(testMemberId, testActivitySubjectPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 false 반환")
    void testIsUnliked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(postLikeJpaRepository.existsByPostIdAndMemberId(testActivitySubjectPostId.getValue(), testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(activitySubjectPostIdRepositoryJpaAdapter.isUnliked(testMemberId, testActivitySubjectPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isBookmarked로 true 반환")
    void testIsBookmarked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(postBookmarkJpaRepository.existsByPostIdAndMemberId(testActivitySubjectPostId.getValue(), testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(activitySubjectPostIdRepositoryJpaAdapter.isBookmarked(testMemberId, testActivitySubjectPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isLiked로 false 반환")
    void testIsBookmarked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(postBookmarkJpaRepository.existsByPostIdAndMemberId(testActivitySubjectPostId.getValue(), testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(activitySubjectPostIdRepositoryJpaAdapter.isBookmarked(testMemberId, testActivitySubjectPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 true 반환")
    void testIsNotBookmarked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(postBookmarkJpaRepository.existsByPostIdAndMemberId(testActivitySubjectPostId.getValue(), testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(activitySubjectPostIdRepositoryJpaAdapter.isNotBookmarked(testMemberId, testActivitySubjectPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 false 반환")
    void testIsNotBookmarked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(postBookmarkJpaRepository.existsByPostIdAndMemberId(testActivitySubjectPostId.getValue(), testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(activitySubjectPostIdRepositoryJpaAdapter.isNotBookmarked(testMemberId, testActivitySubjectPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("존재하는 게시글에 대해 like 실행 시 좋아요 저장 및 카운트 증가")
    void testLike_givenExistedPost_willIncreaseLikeCount() {
        // given
        PostEntity mockPost = mock(PostEntity.class);
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(mockPost));

        // when
        activitySubjectPostIdRepositoryJpaAdapter.like(testMemberId, testActivitySubjectPostId);

        // then
        verify(postLikeJpaRepository, times(1)).save(any(PostLikeEntity.class));
        verify(mockPost, times(1)).increaseLikeCount();
    }

    @Test
    @DisplayName("존재하지 않는 게시글로 인해 like 실행 실패")
    void testLike_givenNotFoundPost_willThrowException() {
        // given
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> activitySubjectPostIdRepositoryJpaAdapter.like(testMemberId, testActivitySubjectPostId));

        // then
        verify(postLikeJpaRepository, times(1)).save(any(PostLikeEntity.class));
    }

    @Test
    @DisplayName("존재하는 게시글에 대해 unlike 실행 시 좋아요 삭제 및 카운트 감소")
    void testUnlike_givenExistedPost_willDecreaseLikeCount() {
        // given
        PostEntity mockPost = mock(PostEntity.class);
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(mockPost));

        // when
        activitySubjectPostIdRepositoryJpaAdapter.unlike(testMemberId, testActivitySubjectPostId);

        // then
        verify(postLikeJpaRepository, times(1)).delete(any(PostLikeEntity.class));
        verify(mockPost, times(1)).decreaseLikeCount();
    }

    @Test
    @DisplayName("존재하지 않는 게시글로 인해 unlike 실행 실패")
    void testUnlike_givenNotFoundPost_willThrowException() {
        // given
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> activitySubjectPostIdRepositoryJpaAdapter.unlike(testMemberId, testActivitySubjectPostId));

        // then
        verify(postLikeJpaRepository, times(1)).delete(any(PostLikeEntity.class));
    }

    @Test
    @DisplayName("bookmark 실행 시 게시글 북마크 정상 저장")
    void testBookmark_givenValidData_willSaveBookmark() {
        // given & when
        activitySubjectPostIdRepositoryJpaAdapter.bookmark(testMemberId, testActivitySubjectPostId);

        // then
        verify(postBookmarkJpaRepository, times(1)).save(any(PostBookmarkEntity.class));
    }

    @Test
    @DisplayName("cancelBookmark 실행 시 게시글 북마크 정상 삭제")
    void testCancelBookmark_givenValidData_willDeleteBookmark() {
        // given & when
        activitySubjectPostIdRepositoryJpaAdapter.cancelBookmark(testMemberId, testActivitySubjectPostId);

        // then
        verify(postBookmarkJpaRepository, times(1)).delete(any(PostBookmarkEntity.class));
    }
}