package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.framework.out.jpa.entity.PostBookmarkEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.PostLikeEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.common.util.PostEntityTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.repository.PostJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetPostIdTestUtils.testTargetPostId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TargetPostRepositoryJpaAdapterTest implements PostEntityTestUtils {
    PostJpaRepository postJpaRepository = Mockito.mock(PostJpaRepository.class);
    PostLikeJpaRepository postLikeJpaRepository = Mockito.mock(PostLikeJpaRepository.class);
    PostBookmarkJpaRepository postBookmarkJpaRepository = Mockito.mock(PostBookmarkJpaRepository.class);
    TargetPostRepositoryJpaAdapter targetPostIdRepositoryJpaAdapter = new TargetPostRepositoryJpaAdapter(postJpaRepository, postLikeJpaRepository, postBookmarkJpaRepository);

    @Test
    @DisplayName("isIdExist로 true 반환")
    void testIsIdExist_givenIdThatExists_willReturnTrue() {
        // given & when
        given(postJpaRepository.existsByUlid(testTargetPostId.getValue())).willReturn(true);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isIdExist(testTargetPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isIdExist로 false 반환")
    void testIsIdExist_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(postJpaRepository.existsByUlid(testTargetPostId.getValue())).willReturn(false);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isIdExist(testTargetPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isPublished로 true 반환")
    void testIsPublished_givenIdThatIsPublished_willReturnTrue() {
        // given & when
        given(postJpaRepository.findByUlid(testTargetPostId.getValue())).willReturn(Optional.of(createPostEntityBuilder().build()));

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isPublished(testTargetPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isPublished로 false 반환")
    void testIsPublished_givenIdThatIsNotPublished_willReturnFalse() {
        // given & when
        given(postJpaRepository.findByUlid(testTargetPostId.getValue())).willReturn(Optional.of(createPostEntityBuilder().isPublished(false).build()));

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isPublished(testTargetPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isLiked로 true 반환")
    void testIsLiked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(postLikeJpaRepository.existsByPostIdAndMemberId(testTargetPostId.getValue(), testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isLiked(testMemberId, testTargetPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isLiked로 false 반환")
    void testIsLiked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(postLikeJpaRepository.existsByPostIdAndMemberId(testTargetPostId.getValue(), testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isLiked(testMemberId, testTargetPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 true 반환")
    void testIsUnliked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(postLikeJpaRepository.existsByPostIdAndMemberId(testTargetPostId.getValue(), testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isUnliked(testMemberId, testTargetPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 false 반환")
    void testIsUnliked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(postLikeJpaRepository.existsByPostIdAndMemberId(testTargetPostId.getValue(), testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isUnliked(testMemberId, testTargetPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isBookmarked로 true 반환")
    void testIsBookmarked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(postBookmarkJpaRepository.existsByPostIdAndMemberId(testTargetPostId.getValue(), testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isBookmarked(testMemberId, testTargetPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isLiked로 false 반환")
    void testIsBookmarked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(postBookmarkJpaRepository.existsByPostIdAndMemberId(testTargetPostId.getValue(), testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isBookmarked(testMemberId, testTargetPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 true 반환")
    void testIsNotBookmarked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(postBookmarkJpaRepository.existsByPostIdAndMemberId(testTargetPostId.getValue(), testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isNotBookmarked(testMemberId, testTargetPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 false 반환")
    void testIsNotBookmarked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(postBookmarkJpaRepository.existsByPostIdAndMemberId(testTargetPostId.getValue(), testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isNotBookmarked(testMemberId, testTargetPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("존재하는 게시글에 대해 like 실행 시 좋아요 저장 및 카운트 증가")
    void testLike_givenExistedPost_willIncreaseLikeCount() {
        // given
        PostEntity mockPost = mock(PostEntity.class);
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(mockPost));

        // when
        targetPostIdRepositoryJpaAdapter.like(testMemberId, testTargetPostId);

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
        assertThrows(NoSuchElementException.class, () -> targetPostIdRepositoryJpaAdapter.like(testMemberId, testTargetPostId));

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
        targetPostIdRepositoryJpaAdapter.unlike(testMemberId, testTargetPostId);

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
        assertThrows(NoSuchElementException.class, () -> targetPostIdRepositoryJpaAdapter.unlike(testMemberId, testTargetPostId));

        // then
        verify(postLikeJpaRepository, times(1)).delete(any(PostLikeEntity.class));
    }

    @Test
    @DisplayName("bookmark 실행 시 게시글 북마크 정상 저장")
    void testBookmark_givenValidData_willSaveBookmark() {
        // given & when
        targetPostIdRepositoryJpaAdapter.bookmark(testMemberId, testTargetPostId);

        // then
        verify(postBookmarkJpaRepository, times(1)).save(any(PostBookmarkEntity.class));
    }

    @Test
    @DisplayName("cancelBookmark 실행 시 게시글 북마크 정상 삭제")
    void testCancelBookmark_givenValidData_willDeleteBookmark() {
        // given & when
        targetPostIdRepositoryJpaAdapter.cancelBookmark(testMemberId, testTargetPostId);

        // then
        verify(postBookmarkJpaRepository, times(1)).delete(any(PostBookmarkEntity.class));
    }
}