package kr.modusplant.domains.communication.qna.persistence.repository;

import kr.modusplant.domains.communication.qna.persistence.entity.QnaLikeEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QnaLikeRepository extends JpaRepository<QnaLikeEntity, QnaLikeId> {
    // 사용자별 Q&A 게시글 좋아요 전체 리스트 조회
    List<QnaLikeEntity> findByMemberId(UUID memberId);

    // 사용자별 Q&A 게시글 좋아요 리스트 조회
    List<QnaLikeEntity> findByMemberIdAndPostIdIn(UUID memberId, List<String> postIds);

    boolean existsByPostIdAndMemberId(String postId, UUID memberId);
    void deleteByPostIdAndMemberId(String postId, UUID memberId);
}
