package kr.modusplant.framework.out.jpa.repository;

import kr.modusplant.framework.out.jpa.entity.CommPostBookmarkEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.CommPostBookmarkEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.shared.persistence.compositekey.CommPostBookmarkId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
public class CommPostBookmarkJpaRepositoryTest implements CommPostBookmarkEntityTestUtils {
    @Autowired
    CommPostBookmarkJpaRepository commPostBookmarkRepository;

    @Nested
    @DisplayName("setUp 사용 테스트 그룹")
    class SetupTest {
        private String postId;
        private UUID memberId;

        @BeforeEach
        void setUp() {
            // given
            postId = TEST_COMM_POST_ULID;
            memberId = createMemberBasicUserEntityWithUuid().getUuid();
        }

        @Test
        @DisplayName("게시글 북마크 후 조회")
        void likeCommPost_success() {
            // when
            commPostBookmarkRepository.save(CommPostBookmarkEntity.of(postId, memberId));

            // then
            Optional<CommPostBookmarkEntity> commBookmarkEntity = commPostBookmarkRepository.findById(new CommPostBookmarkId(postId, memberId));
            assertThat(commBookmarkEntity).isPresent();
            assertThat(commBookmarkEntity.get().getPostId()).isEqualTo(postId);
            assertThat(commBookmarkEntity.get().getMemberId()).isEqualTo(memberId);
            assertThat(commBookmarkEntity.get().getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("특정 사용자 게시글 북마크 여부 확인")
        void isBookmarkdByMember_willReturnTrue() {
            // given
            commPostBookmarkRepository.save(CommPostBookmarkEntity.of(postId, memberId));

            // when
            boolean isBookmarkd = commPostBookmarkRepository.existsByPostIdAndMemberId(postId, memberId);

            // then
            assertThat(isBookmarkd).isTrue();
        }

        @Test
        @DisplayName("게시글 북마크 취소")
        void unlikeCommPost_success() {
            // given
            commPostBookmarkRepository.save(CommPostBookmarkEntity.of(postId, memberId));

            // when
            commPostBookmarkRepository.deleteByPostIdAndMemberId(postId, memberId);

            // then
            assertThat(commPostBookmarkRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
        }
    }

    @Test
    @DisplayName("사용자별 게시글 북마크 전체 리스트 조회")
    void findCommPostBookmarksByMemberId() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> postIds = List.of(
                "TEST_QNA_POST_ID_001",
                "TEST_QNA_POST_ID_002",
                "TEST_QNA_POST_ID_003"
        );

        commPostBookmarkRepository.saveAll(List.of(
                CommPostBookmarkEntity.of(postIds.get(0), memberId),
                CommPostBookmarkEntity.of(postIds.get(1), memberId),
                CommPostBookmarkEntity.of(postIds.get(2), memberId)
        ));
        commPostBookmarkRepository.flush();

        // when
        List<CommPostBookmarkEntity> commBookmarkList = commPostBookmarkRepository.findByMemberId(memberId);

        assertThat(commBookmarkList).hasSize(postIds.size());
    }

    @Test
    @DisplayName("사용자별 게시글 북마크 리스트 조회")
    void findCommPostBookmarksByMemberIdAndPostIds() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> postIds = List.of(
                "TEST_QNA_POST_ID_001",
                "TEST_QNA_POST_ID_002",
                "TEST_QNA_POST_ID_003"
        );

        commPostBookmarkRepository.saveAll(List.of(
                CommPostBookmarkEntity.of(postIds.get(0), memberId),
                CommPostBookmarkEntity.of(postIds.get(1), memberId),
                CommPostBookmarkEntity.of(postIds.get(2), memberId)
        ));
        commPostBookmarkRepository.flush();

        // when
        List<CommPostBookmarkEntity> commBookmarkList = commPostBookmarkRepository.findByMemberIdAndPostIdIn(memberId, postIds);

        // then
        List<String> likedPostIds = commBookmarkList.stream()
                .map(CommPostBookmarkEntity::getPostId)
                .toList();

        assertThat(commBookmarkList).size().isEqualTo(postIds.size());
        assertThat(likedPostIds).hasSize(postIds.size());
        assertThat(likedPostIds).containsExactlyInAnyOrder(postIds.get(0), postIds.get(1), postIds.get(2));
    }
}
