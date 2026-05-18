package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.framework.out.jpa.entity.PostBookmarkEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.PostBookmarkEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.shared.persistence.compositekey.PostBookmarkCompositeKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RepositoryOnlyContext
public class PostBookmarkJpaRepositoryTest implements PostBookmarkEntityTestUtils {
    @Autowired
    PostBookmarkJpaRepository postBookmarkRepository;

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
            postBookmarkRepository.save(PostBookmarkEntity.of(postId, memberId));

            // then
            Optional<PostBookmarkEntity> bookmarkEntity = postBookmarkRepository.findById(new PostBookmarkCompositeKey(postId, memberId));
            assertThat(bookmarkEntity).isPresent();
            assertThat(bookmarkEntity.get().getPostId()).isEqualTo(postId);
            assertThat(bookmarkEntity.get().getMemberId()).isEqualTo(memberId);
            assertThat(bookmarkEntity.get().getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("특정 사용자 게시글 북마크 여부 확인")
        void isBookmarkedByMember_willReturnTrue() {
            // given
            postBookmarkRepository.save(PostBookmarkEntity.of(postId, memberId));

            // when
            boolean isBookmarked = postBookmarkRepository.existsByPostIdAndMemberId(postId, memberId);

            // then
            assertThat(isBookmarked).isTrue();
        }

        @Test
        @DisplayName("게시글 북마크 취소")
        void unlikeCommPost_success() {
            // given
            postBookmarkRepository.save(PostBookmarkEntity.of(postId, memberId));

            // when
            postBookmarkRepository.deleteByPostIdAndMemberId(postId, memberId);

            // then
            assertThat(postBookmarkRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
        }

        @Test
        @DisplayName("게시글 북마크 엔터티 toString 호출 시 순환 오류 발생 여부 확인")
        void testToString_givenCommPostBookmarkEntity_willReturnRepresentative() {
            // given & when
            PostBookmarkEntity entity = postBookmarkRepository.save(PostBookmarkEntity.of(postId, memberId));

            // then
            assertDoesNotThrow(entity::toString);
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

        postBookmarkRepository.saveAll(List.of(
                PostBookmarkEntity.of(postIds.get(0), memberId),
                PostBookmarkEntity.of(postIds.get(1), memberId),
                PostBookmarkEntity.of(postIds.get(2), memberId)
        ));
        postBookmarkRepository.flush();

        // when
        List<PostBookmarkEntity> commBookmarkList = postBookmarkRepository.findByMemberId(memberId);

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

        postBookmarkRepository.saveAll(List.of(
                PostBookmarkEntity.of(postIds.get(0), memberId),
                PostBookmarkEntity.of(postIds.get(1), memberId),
                PostBookmarkEntity.of(postIds.get(2), memberId)
        ));
        postBookmarkRepository.flush();

        // when
        List<PostBookmarkEntity> commBookmarkList = postBookmarkRepository.findByMemberIdAndPostIdIn(memberId, postIds);

        // then
        List<String> likedPostIds = commBookmarkList.stream()
                .map(PostBookmarkEntity::getPostId)
                .toList();

        assertThat(commBookmarkList).size().isEqualTo(postIds.size());
        assertThat(likedPostIds).hasSize(postIds.size());
        assertThat(likedPostIds).containsExactlyInAnyOrder(postIds.get(0), postIds.get(1), postIds.get(2));
    }
}
