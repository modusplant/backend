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
        private String convPostId;
        private UUID memberId;

        @BeforeEach
        void setUp() {
            // given
            convPostId = convPostWithUlid.getUlid();
            memberId = createMemberBasicUserEntityWithUuid().getUuid();
        }

        @Test
        @DisplayName("대화 게시글 좋아요 후 조회")
        void likeConvPost_success() {
            // when
            convLikeRepository.save(ConvLikeEntity.of(convPostId, memberId));

            // then
            Optional<ConvLikeEntity> convLikeEntity = convLikeRepository.findById(new ConvLikeId(convPostId, memberId));
            assertThat(convLikeEntity).isPresent();
            assertThat(convLikeEntity.get().getConvPostId()).isEqualTo(convPostId);
            assertThat(convLikeEntity.get().getMemberId()).isEqualTo(memberId);
            assertThat(convLikeEntity.get().getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("특정 사용자 대화 게시글 좋아요 여부 확인")
        void isLikedByMember_returnsTrue() {
            // given
            convLikeRepository.save(ConvLikeEntity.of(convPostId, memberId));

            // when
            boolean isLiked = convLikeRepository.existsByConvPostIdAndMemberId(convPostId, memberId);

            // then
            assertThat(isLiked).isTrue();
        }

        @Test
        @DisplayName("대화 게시글 좋아요 취소")
        void unlikeConvPost_success() {
            // given
            convLikeRepository.save(ConvLikeEntity.of(convPostId, memberId));

            // when
            convLikeRepository.deleteByConvPostIdAndMemberId(convPostId, memberId);

            // then
            assertThat(convLikeRepository.existsByConvPostIdAndMemberId(convPostId, memberId)).isFalse();
        }
    }

    @Test
    @DisplayName("사용자별 대화 게시글 좋아요 전체 리스트 조회")
    void findConvLikesByMemberId() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> convPostIds = List.of(
                "TEST_CONV_POST_ID_001",
                "TEST_CONV_POST_ID_002",
                "TEST_CONV_POST_ID_003"
        );

        convLikeRepository.saveAll(List.of(
                ConvLikeEntity.of(convPostIds.get(0), memberId),
                ConvLikeEntity.of(convPostIds.get(1), memberId),
                ConvLikeEntity.of(convPostIds.get(2), memberId)
        ));
        convLikeRepository.flush();

        // when
        List<ConvLikeEntity> convLikeList = convLikeRepository.findByMemberId(memberId);

        assertThat(convLikeList).hasSize(convPostIds.size());
    }

    @Test
    @DisplayName("사용자별 대화 게시글 좋아요 리스트 조회")
    void findConvLikesByMemberIdAndPostIds() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> convPostIds = List.of(
                "TEST_CONV_POST_ID_001",
                "TEST_CONV_POST_ID_002",
                "TEST_CONV_POST_ID_003"
        );

        convLikeRepository.saveAll(List.of(
                ConvLikeEntity.of(convPostIds.get(0), memberId),
                ConvLikeEntity.of(convPostIds.get(1), memberId),
                ConvLikeEntity.of(convPostIds.get(2), memberId)
        ));
        convLikeRepository.flush();

        // when
        List<ConvLikeEntity> convLikeList = convLikeRepository.findByMemberIdAndConvPostIdIn(memberId, convPostIds);

        // then
        List<String> likedConvPostIds = convLikeList.stream()
                .map(ConvLikeEntity::getConvPostId)
                .toList();

        assertThat(convLikeList).size().isEqualTo(convPostIds.size());
        assertThat(likedConvPostIds).hasSize(convPostIds.size());
        assertThat(likedConvPostIds).containsExactlyInAnyOrder(convPostIds.get(0), convPostIds.get(1), convPostIds.get(2));
    }
}
