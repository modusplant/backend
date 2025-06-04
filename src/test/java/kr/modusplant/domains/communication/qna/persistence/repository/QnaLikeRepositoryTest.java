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
        private String qnaPostId;
        private UUID memberId;

        @BeforeEach
        void setUp() {
            // given
            qnaPostId = qnaPostWithUlid.getUlid();
            memberId = createMemberBasicUserEntityWithUuid().getUuid();
        }

        @Test
        @DisplayName("Q&A 게시글 좋아요 후 조회")
        void likeQnaPost_success() {
            // when
            qnaLikeRepository.save(QnaLikeEntity.of(qnaPostId, memberId));

            // then
            Optional<QnaLikeEntity> qnaLikeEntity = qnaLikeRepository.findById(new QnaLikeId(qnaPostId, memberId));
            assertThat(qnaLikeEntity).isPresent();
            assertThat(qnaLikeEntity.get().getQnaPostId()).isEqualTo(qnaPostId);
            assertThat(qnaLikeEntity.get().getMemberId()).isEqualTo(memberId);
            assertThat(qnaLikeEntity.get().getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("특정 사용자 Q&A 게시글 좋아요 여부 확인")
        void isLikedByMember_returnsTrue() {
            // given
            qnaLikeRepository.save(QnaLikeEntity.of(qnaPostId, memberId));

            // when
            boolean isLiked = qnaLikeRepository.existsByQnaPostIdAndMemberId(qnaPostId, memberId);

            // then
            assertThat(isLiked).isTrue();
        }

        @Test
        @DisplayName("Q&A 게시글 좋아요 취소")
        void unlikeQnaPost_success() {
            // given
            qnaLikeRepository.save(QnaLikeEntity.of(qnaPostId, memberId));

            // when
            qnaLikeRepository.deleteByQnaPostIdAndMemberId(qnaPostId, memberId);

            // then
            assertThat(qnaLikeRepository.existsByQnaPostIdAndMemberId(qnaPostId, memberId)).isFalse();
        }
    }

    @Test
    @DisplayName("사용자별 Q&A 게시글 좋아요 전체 리스트 조회")
    void findQnaLikesByMemberId() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> qnaPostIds = List.of(
                "TEST_QNA_POST_ID_001",
                "TEST_QNA_POST_ID_002",
                "TEST_QNA_POST_ID_003"
        );

        qnaLikeRepository.saveAll(List.of(
                QnaLikeEntity.of(qnaPostIds.get(0), memberId),
                QnaLikeEntity.of(qnaPostIds.get(1), memberId),
                QnaLikeEntity.of(qnaPostIds.get(2), memberId)
        ));
        qnaLikeRepository.flush();

        // when
        List<QnaLikeEntity> qnaLikeList = qnaLikeRepository.findByMemberId(memberId);

        assertThat(qnaLikeList).hasSize(qnaPostIds.size());
    }

    @Test
    @DisplayName("사용자별 Q&A 게시글 좋아요 리스트 조회")
    void findQnaLikesByMemberIdAndPostIds() {
        // given
        UUID memberId = createMemberBasicUserEntityWithUuid().getUuid();
        List<String> qnaPostIds = List.of(
                "TEST_QNA_POST_ID_001",
                "TEST_QNA_POST_ID_002",
                "TEST_QNA_POST_ID_003"
        );

        qnaLikeRepository.saveAll(List.of(
                QnaLikeEntity.of(qnaPostIds.get(0), memberId),
                QnaLikeEntity.of(qnaPostIds.get(1), memberId),
                QnaLikeEntity.of(qnaPostIds.get(2), memberId)
        ));
        qnaLikeRepository.flush();

        // when
        List<QnaLikeEntity> qnaLikeList = qnaLikeRepository.findByMemberIdAndQnaPostIdIn(memberId, qnaPostIds);

        // then
        List<String> likedQnaPostIds = qnaLikeList.stream()
                .map(QnaLikeEntity::getQnaPostId)
                .toList();

        assertThat(qnaLikeList).size().isEqualTo(qnaPostIds.size());
        assertThat(likedQnaPostIds).hasSize(qnaPostIds.size());
        assertThat(likedQnaPostIds).containsExactlyInAnyOrder(qnaPostIds.get(0), qnaPostIds.get(1), qnaPostIds.get(2));
    }
}
