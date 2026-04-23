package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.framework.jpa.entity.common.util.CommPostEntityTestUtils;
import kr.modusplant.framework.jpa.repository.CommPostBookmarkJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostLikeJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetPostIdTestUtils.testTargetPostId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class TargetPostRepositoryJpaAdapterTest implements CommPostEntityTestUtils {
    CommPostJpaRepository postJpaRepository = Mockito.mock(CommPostJpaRepository.class);
    CommPostLikeJpaRepository postLikeJpaRepository = Mockito.mock(CommPostLikeJpaRepository.class);
    CommPostBookmarkJpaRepository postBookmarkJpaRepository = Mockito.mock(CommPostBookmarkJpaRepository.class);
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
        given(postJpaRepository.findByUlid(testTargetPostId.getValue())).willReturn(Optional.of(createCommPostEntityBuilder().build()));

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isPublished(testTargetPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isPublished로 false 반환")
    void testIsPublished_givenIdThatIsNotPublished_willReturnFalse() {
        // given & when
        given(postJpaRepository.findByUlid(testTargetPostId.getValue())).willReturn(Optional.of(createCommPostEntityBuilder().isPublished(false).build()));

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
}