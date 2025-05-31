package kr.modusplant.domains.communication.tip.persistence.repository;

import kr.modusplant.domains.communication.tip.common.util.entity.TipLikeEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeId;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
public class TipLikeRepositoryTest implements TipLikeEntityTestUtils {
    @Autowired
    TipLikeRepository tipLikeRepository;

    @Nested
    @DisplayName("setUp 사용 테스트 그룹")
    class SetupTest {
        private String tipPostId;
        private UUID memberId;

        @BeforeEach
        void setUp() {
            // given
            tipPostId = tipPostWithUlid.getUlid();
            memberId = createMemberBasicUserEntityWithUuid().getUuid();
        }

        @Test
        @DisplayName("팁 게시글 좋아요 후 조회")
        void likeTipPost_success() {
            // when
            tipLikeRepository.save(TipLikeEntity.of(tipPostId, memberId));

            // then
            Optional<TipLikeEntity> tipLikeEntity = tipLikeRepository.findById(new TipLikeId(tipPostId, memberId));
            assertThat(tipLikeEntity).isPresent();
            assertThat(tipLikeEntity.get().getTipPostId()).isEqualTo(tipPostId);
            assertThat(tipLikeEntity.get().getMemberId()).isEqualTo(memberId);
            assertThat(tipLikeEntity.get().getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("특정 사용자 팁 게시글 좋아요 여부 확인")
        void isLikedByMember_returnsTrue() {
            // given
            tipLikeRepository.save(TipLikeEntity.of(tipPostId, memberId));

            // when
            boolean isLiked = tipLikeRepository.existsByTipPostIdAndMemberId(tipPostId, memberId);

            // then
            assertThat(isLiked).isTrue();
        }

        @Test
        @DisplayName("팁 게시글 좋아요 취소")
        void unlikeTipPost_success() {
            // given
            tipLikeRepository.save(TipLikeEntity.of(tipPostId, memberId));

            // when
            tipLikeRepository.deleteByTipPostIdAndMemberId(tipPostId, memberId);

            // then
            assertThat(tipLikeRepository.existsByTipPostIdAndMemberId(tipPostId, memberId)).isFalse();
        }
    }

    @Test
    @DisplayName("사용자별 팁 게시글 좋아요 전체 리스트 조회")
    void findTipLikesByMemberId() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> tipPostIds = List.of(
                "TEST_TIP_POST_ID_001",
                "TEST_TIP_POST_ID_002",
                "TEST_TIP_POST_ID_003"
        );

        tipLikeRepository.saveAll(List.of(
                TipLikeEntity.of(tipPostIds.get(0), memberId),
                TipLikeEntity.of(tipPostIds.get(1), memberId),
                TipLikeEntity.of(tipPostIds.get(2), memberId)
        ));
        tipLikeRepository.flush();

        // when
        List<TipLikeEntity> tipLikeList = tipLikeRepository.findByMemberId(memberId);

        assertThat(tipLikeList).hasSize(tipPostIds.size());
    }

    @Test
    @DisplayName("사용자별 팁 게시글 좋아요 리스트 조회")
    void findTipLikesByMemberIdAndPostIds() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> tipPostIds = List.of(
                "TEST_TIP_POST_ID_001",
                "TEST_TIP_POST_ID_002",
                "TEST_TIP_POST_ID_003"
        );

        tipLikeRepository.saveAll(List.of(
                TipLikeEntity.of(tipPostIds.get(0), memberId),
                TipLikeEntity.of(tipPostIds.get(1), memberId),
                TipLikeEntity.of(tipPostIds.get(2), memberId)
        ));
        tipLikeRepository.flush();

        // when
        List<TipLikeEntity> tipLikeList = tipLikeRepository.findByMemberIdAndTipPostIdIn(memberId, tipPostIds);

        // then
        List<String> likedTipPostIds = tipLikeList.stream()
                .map(TipLikeEntity::getTipPostId)
                .toList();

        assertThat(tipLikeList).size().isEqualTo(tipPostIds.size());
        assertThat(likedTipPostIds).hasSize(tipPostIds.size());
        assertThat(likedTipPostIds).containsExactlyInAnyOrder(tipPostIds.get(0), tipPostIds.get(1), tipPostIds.get(2));
    }
}
