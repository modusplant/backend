package kr.modusplant.domains.communication.conversation.persistence.repository;

import kr.modusplant.domains.communication.conversation.common.util.entity.ConvLikeEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvLikeEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvLikeId;
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
public class ConvLikeRepositoryTest implements ConvLikeEntityTestUtils {
    @Autowired
    ConvLikeRepository convLikeRepository;

    @Nested
    @DisplayName("setUp 사용 테스트 그룹")
    class SetupTest {
        private String postId;
        private UUID memberId;

        @BeforeEach
        void setUp() {
            // given
            postId = convPostWithUlid.getUlid();
            memberId = createMemberBasicUserEntityWithUuid().getUuid();
        }

        @Test
        @DisplayName("대화 게시글 좋아요 후 조회")
        void likeConvPost_success() {
            // when
            convLikeRepository.save(ConvLikeEntity.of(postId, memberId));

            // then
            Optional<ConvLikeEntity> convLikeEntity = convLikeRepository.findById(new ConvLikeId(postId, memberId));
            assertThat(convLikeEntity).isPresent();
            assertThat(convLikeEntity.get().getPostId()).isEqualTo(postId);
            assertThat(convLikeEntity.get().getMemberId()).isEqualTo(memberId);
            assertThat(convLikeEntity.get().getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("특정 사용자 대화 게시글 좋아요 여부 확인")
        void isLikedByMember_returnsTrue() {
            // given
            convLikeRepository.save(ConvLikeEntity.of(postId, memberId));

            // when
            boolean isLiked = convLikeRepository.existsByPostIdAndMemberId(postId, memberId);

            // then
            assertThat(isLiked).isTrue();
        }

        @Test
        @DisplayName("대화 게시글 좋아요 취소")
        void unlikeConvPost_success() {
            // given
            convLikeRepository.save(ConvLikeEntity.of(postId, memberId));

            // when
            convLikeRepository.deleteByPostIdAndMemberId(postId, memberId);

            // then
            assertThat(convLikeRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
        }
    }

    @Test
    @DisplayName("사용자별 대화 게시글 좋아요 전체 리스트 조회")
    void findConvLikesByMemberId() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> postIds = List.of(
                "TEST_CONV_POST_ID_001",
                "TEST_CONV_POST_ID_002",
                "TEST_CONV_POST_ID_003"
        );

        convLikeRepository.saveAll(List.of(
                ConvLikeEntity.of(postIds.get(0), memberId),
                ConvLikeEntity.of(postIds.get(1), memberId),
                ConvLikeEntity.of(postIds.get(2), memberId)
        ));
        convLikeRepository.flush();

        // when
        List<ConvLikeEntity> convLikeList = convLikeRepository.findByMemberId(memberId);

        assertThat(convLikeList).hasSize(postIds.size());
    }

    @Test
    @DisplayName("사용자별 대화 게시글 좋아요 리스트 조회")
    void findConvLikesByMemberIdAndPostIds() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> postIds = List.of(
                "TEST_CONV_POST_ID_001",
                "TEST_CONV_POST_ID_002",
                "TEST_CONV_POST_ID_003"
        );

        convLikeRepository.saveAll(List.of(
                ConvLikeEntity.of(postIds.get(0), memberId),
                ConvLikeEntity.of(postIds.get(1), memberId),
                ConvLikeEntity.of(postIds.get(2), memberId)
        ));
        convLikeRepository.flush();

        // when
        List<ConvLikeEntity> convLikeList = convLikeRepository.findByMemberIdAndPostIdIn(memberId, postIds);

        // then
        List<String> likedPostIds = convLikeList.stream()
                .map(ConvLikeEntity::getPostId)
                .toList();

        assertThat(convLikeList).size().isEqualTo(postIds.size());
        assertThat(likedPostIds).hasSize(postIds.size());
        assertThat(likedPostIds).containsExactlyInAnyOrder(postIds.get(0), postIds.get(1), postIds.get(2));
    }
}
