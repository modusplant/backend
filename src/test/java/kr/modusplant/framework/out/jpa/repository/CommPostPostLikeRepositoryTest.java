package kr.modusplant.framework.out.jpa.repository;

import kr.modusplant.framework.out.jpa.entity.CommPostLikeEntity;
import kr.modusplant.framework.out.jpa.entity.compositekey.CommPostLikeId;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommPostLikeEntityTestUtils;
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
public class CommPostPostLikeRepositoryTest implements CommPostLikeEntityTestUtils {
    @Autowired
    CommPostLikeRepository commPostLikeRepository;

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
        @DisplayName("소통 게시글 좋아요 후 조회")
        void likeCommPost_success() {
            // when
            commPostLikeRepository.save(CommPostLikeEntity.of(postId, memberId));

            // then
            Optional<CommPostLikeEntity> commLikeEntity = commPostLikeRepository.findById(new CommPostLikeId(postId, memberId));
            assertThat(commLikeEntity).isPresent();
            assertThat(commLikeEntity.get().getPostId()).isEqualTo(postId);
            assertThat(commLikeEntity.get().getMemberId()).isEqualTo(memberId);
            assertThat(commLikeEntity.get().getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("특정 사용자 소통 게시글 좋아요 여부 확인")
        void isLikedByMember_willReturnTrue() {
            // given
            commPostLikeRepository.save(CommPostLikeEntity.of(postId, memberId));

            // when
            boolean isLiked = commPostLikeRepository.existsByPostIdAndMemberId(postId, memberId);

            // then
            assertThat(isLiked).isTrue();
        }

        @Test
        @DisplayName("소통 게시글 좋아요 취소")
        void unlikeCommPost_success() {
            // given
            commPostLikeRepository.save(CommPostLikeEntity.of(postId, memberId));

            // when
            commPostLikeRepository.deleteByPostIdAndMemberId(postId, memberId);

            // then
            assertThat(commPostLikeRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
        }
    }

    @Test
    @DisplayName("사용자별 소통 게시글 좋아요 전체 리스트 조회")
    void findCommPostLikesByMemberId() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> postIds = List.of(
                "TEST_QNA_POST_ID_001",
                "TEST_QNA_POST_ID_002",
                "TEST_QNA_POST_ID_003"
        );

        commPostLikeRepository.saveAll(List.of(
                CommPostLikeEntity.of(postIds.get(0), memberId),
                CommPostLikeEntity.of(postIds.get(1), memberId),
                CommPostLikeEntity.of(postIds.get(2), memberId)
        ));
        commPostLikeRepository.flush();

        // when
        List<CommPostLikeEntity> commLikeList = commPostLikeRepository.findByMemberId(memberId);

        assertThat(commLikeList).hasSize(postIds.size());
    }

    @Test
    @DisplayName("사용자별 소통 게시글 좋아요 리스트 조회")
    void findCommPostLikesByMemberIdAndPostIds() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> postIds = List.of(
                "TEST_QNA_POST_ID_001",
                "TEST_QNA_POST_ID_002",
                "TEST_QNA_POST_ID_003"
        );

        commPostLikeRepository.saveAll(List.of(
                CommPostLikeEntity.of(postIds.get(0), memberId),
                CommPostLikeEntity.of(postIds.get(1), memberId),
                CommPostLikeEntity.of(postIds.get(2), memberId)
        ));
        commPostLikeRepository.flush();

        // when
        List<CommPostLikeEntity> commLikeList = commPostLikeRepository.findByMemberIdAndPostIdIn(memberId, postIds);

        // then
        List<String> likedPostIds = commLikeList.stream()
                .map(CommPostLikeEntity::getPostId)
                .toList();

        assertThat(commLikeList).size().isEqualTo(postIds.size());
        assertThat(likedPostIds).hasSize(postIds.size());
        assertThat(likedPostIds).containsExactlyInAnyOrder(postIds.get(0), postIds.get(1), postIds.get(2));
    }
}
