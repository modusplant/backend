package kr.modusplant.domains.communication.qna.persistence.repository;

import kr.modusplant.domains.communication.qna.common.util.entity.QnaLikeEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaLikeEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaLikeId;
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
public class QnaLikeRepositoryTest implements QnaLikeEntityTestUtils {
    @Autowired
    QnaLikeRepository qnaLikeRepository;

    @Nested
    @DisplayName("setUp 사용 테스트 그룹")
    class SetupTest {
        private String postId;
        private UUID memberId;

        @BeforeEach
        void setUp() {
            // given
            postId = testQnaPostWithUlid.getUlid();
            memberId = createMemberBasicUserEntityWithUuid().getUuid();
        }

        @Test
        @DisplayName("Q&A 게시글 좋아요 후 조회")
        void likeQnaPost_success() {
            // when
            qnaLikeRepository.save(QnaLikeEntity.of(postId, memberId));

            // then
            Optional<QnaLikeEntity> qnaLikeEntity = qnaLikeRepository.findById(new QnaLikeId(postId, memberId));
            assertThat(qnaLikeEntity).isPresent();
            assertThat(qnaLikeEntity.get().getPostId()).isEqualTo(postId);
            assertThat(qnaLikeEntity.get().getMemberId()).isEqualTo(memberId);
            assertThat(qnaLikeEntity.get().getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("특정 사용자 Q&A 게시글 좋아요 여부 확인")
        void isLikedByMember_returnsTrue() {
            // given
            qnaLikeRepository.save(QnaLikeEntity.of(postId, memberId));

            // when
            boolean isLiked = qnaLikeRepository.existsByPostIdAndMemberId(postId, memberId);

            // then
            assertThat(isLiked).isTrue();
        }

        @Test
        @DisplayName("Q&A 게시글 좋아요 취소")
        void unlikeQnaPost_success() {
            // given
            qnaLikeRepository.save(QnaLikeEntity.of(postId, memberId));

            // when
            qnaLikeRepository.deleteByPostIdAndMemberId(postId, memberId);

            // then
            assertThat(qnaLikeRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
        }
    }

    @Test
    @DisplayName("사용자별 Q&A 게시글 좋아요 전체 리스트 조회")
    void findQnaLikesByMemberId() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> postIds = List.of(
                "TEST_QNA_POST_ID_001",
                "TEST_QNA_POST_ID_002",
                "TEST_QNA_POST_ID_003"
        );

        qnaLikeRepository.saveAll(List.of(
                QnaLikeEntity.of(postIds.get(0), memberId),
                QnaLikeEntity.of(postIds.get(1), memberId),
                QnaLikeEntity.of(postIds.get(2), memberId)
        ));
        qnaLikeRepository.flush();

        // when
        List<QnaLikeEntity> qnaLikeList = qnaLikeRepository.findByMemberId(memberId);

        assertThat(qnaLikeList).hasSize(postIds.size());
    }

    @Test
    @DisplayName("사용자별 Q&A 게시글 좋아요 리스트 조회")
    void findQnaLikesByMemberIdAndPostIds() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> postIds = List.of(
                "TEST_QNA_POST_ID_001",
                "TEST_QNA_POST_ID_002",
                "TEST_QNA_POST_ID_003"
        );

        qnaLikeRepository.saveAll(List.of(
                QnaLikeEntity.of(postIds.get(0), memberId),
                QnaLikeEntity.of(postIds.get(1), memberId),
                QnaLikeEntity.of(postIds.get(2), memberId)
        ));
        qnaLikeRepository.flush();

        // when
        List<QnaLikeEntity> qnaLikeList = qnaLikeRepository.findByMemberIdAndPostIdIn(memberId, postIds);

        // then
        List<String> likedPostIds = qnaLikeList.stream()
                .map(QnaLikeEntity::getPostId)
                .toList();

        assertThat(qnaLikeList).size().isEqualTo(postIds.size());
        assertThat(likedPostIds).hasSize(postIds.size());
        assertThat(likedPostIds).containsExactlyInAnyOrder(postIds.get(0), postIds.get(1), postIds.get(2));
    }
}
