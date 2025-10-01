package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.framework.out.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostLikeJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetPostIdTestUtils.testTargetPostId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class TargetPostIdRepositoryJpaAdapterTest {
    CommPostJpaRepository commPostJpaRepository = Mockito.mock(CommPostJpaRepository.class);
    CommPostLikeJpaRepository commPostLikeJpaRepository = Mockito.mock(CommPostLikeJpaRepository.class);
    TargetPostIdRepositoryJpaAdapter targetPostIdRepositoryJpaAdapter = new TargetPostIdRepositoryJpaAdapter(commPostJpaRepository, commPostLikeJpaRepository);

    @Test
    @DisplayName("isIdExist로 true 반환")
    void testIsIdExist_givenIdThatExists_willReturnTrue() {
        // given & when
        given(commPostJpaRepository.existsByUlid(testTargetPostId.getValue())).willReturn(true);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isIdExist(testTargetPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isIdExist로 false 반환")
    void testIsIdExist_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(commPostJpaRepository.existsByUlid(testTargetPostId.getValue())).willReturn(false);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isIdExist(testTargetPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isLiked로 true 반환")
    void testIsLiked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(commPostLikeJpaRepository.existsByPostIdAndMemberId(testTargetPostId.getValue(), testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isLiked(testMemberId, testTargetPostId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isLiked로 false 반환")
    void testIsLiked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(commPostLikeJpaRepository.existsByPostIdAndMemberId(testTargetPostId.getValue(), testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isLiked(testMemberId, testTargetPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 true 반환")
    void testIsUnliked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(commPostLikeJpaRepository.existsByPostIdAndMemberId(testTargetPostId.getValue(), testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isUnliked(testMemberId, testTargetPostId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 false 반환")
    void testIsUnliked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(commPostLikeJpaRepository.existsByPostIdAndMemberId(testTargetPostId.getValue(), testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isUnliked(testMemberId, testTargetPostId)).isEqualTo(true);
    }
}