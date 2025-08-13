package kr.modusplant.legacy.domains.communication.persistence.repository;

import kr.modusplant.global.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommLikeEntityTestUtils;
import kr.modusplant.legacy.domains.communication.persistence.entity.CommLikeEntity;
import kr.modusplant.legacy.domains.communication.persistence.entity.CommLikeId;
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
public class CommLikeRepositoryTest implements CommLikeEntityTestUtils {
    @Autowired
    CommLikeRepository commLikeRepository;

    @Nested
    @DisplayName("setUp 사용 테스트 그룹")
    class SetupTest {
        private String postId;
        private UUID memberId;

        @BeforeEach
        void setUp() {
            // given
            postId = TEST_COMM_POST_WITH_ULID.getUlid();
            memberId = createMemberBasicUserEntityWithUuid().getUuid();
        }

        @Test
        @DisplayName("컨텐츠 게시글 좋아요 후 조회")
        void likeCommPost_success() {
            // when
            commLikeRepository.save(CommLikeEntity.of(postId, memberId));

            // then
            Optional<CommLikeEntity> commLikeEntity = commLikeRepository.findById(new CommLikeId(postId, memberId));
            assertThat(commLikeEntity).isPresent();
            assertThat(commLikeEntity.get().getPostId()).isEqualTo(postId);
            assertThat(commLikeEntity.get().getMemberId()).isEqualTo(memberId);
            assertThat(commLikeEntity.get().getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("특정 사용자 컨텐츠 게시글 좋아요 여부 확인")
        void isLikedByMember_returnsTrue() {
            // given
            commLikeRepository.save(CommLikeEntity.of(postId, memberId));

            // when
            boolean isLiked = commLikeRepository.existsByPostIdAndMemberId(postId, memberId);

            // then
            assertThat(isLiked).isTrue();
        }

        @Test
        @DisplayName("컨텐츠 게시글 좋아요 취소")
        void unlikeCommPost_success() {
            // given
            commLikeRepository.save(CommLikeEntity.of(postId, memberId));

            // when
            commLikeRepository.deleteByPostIdAndMemberId(postId, memberId);

            // then
            assertThat(commLikeRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
        }
    }

    @Test
    @DisplayName("사용자별 컨텐츠 게시글 좋아요 전체 리스트 조회")
    void findCommLikesByMemberId() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> postIds = List.of(
                "TEST_QNA_POST_ID_001",
                "TEST_QNA_POST_ID_002",
                "TEST_QNA_POST_ID_003"
        );

        commLikeRepository.saveAll(List.of(
                CommLikeEntity.of(postIds.get(0), memberId),
                CommLikeEntity.of(postIds.get(1), memberId),
                CommLikeEntity.of(postIds.get(2), memberId)
        ));
        commLikeRepository.flush();

        // when
        List<CommLikeEntity> commLikeList = commLikeRepository.findByMemberId(memberId);

        assertThat(commLikeList).hasSize(postIds.size());
    }

    @Test
    @DisplayName("사용자별 컨텐츠 게시글 좋아요 리스트 조회")
    void findCommLikesByMemberIdAndPostIds() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> postIds = List.of(
                "TEST_QNA_POST_ID_001",
                "TEST_QNA_POST_ID_002",
                "TEST_QNA_POST_ID_003"
        );

        commLikeRepository.saveAll(List.of(
                CommLikeEntity.of(postIds.get(0), memberId),
                CommLikeEntity.of(postIds.get(1), memberId),
                CommLikeEntity.of(postIds.get(2), memberId)
        ));
        commLikeRepository.flush();

        // when
        List<CommLikeEntity> commLikeList = commLikeRepository.findByMemberIdAndPostIdIn(memberId, postIds);

        // then
        List<String> likedPostIds = commLikeList.stream()
                .map(CommLikeEntity::getPostId)
                .toList();

        assertThat(commLikeList).size().isEqualTo(postIds.size());
        assertThat(likedPostIds).hasSize(postIds.size());
        assertThat(likedPostIds).containsExactlyInAnyOrder(postIds.get(0), postIds.get(1), postIds.get(2));
    }
}
