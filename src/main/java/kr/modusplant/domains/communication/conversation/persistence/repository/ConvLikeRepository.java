package kr.modusplant.domains.communication.conversation.persistence.repository;

import kr.modusplant.domains.communication.conversation.persistence.entity.ConvLikeEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConvLikeRepository extends JpaRepository<ConvLikeEntity, ConvLikeId> {
    // 사용자별 대화 게시글 좋아요 전체 리스트 조회
    List<ConvLikeEntity> findByMemberId(UUID memberId);

    // 사용자별 대화 게시글 좋아요 리스트 조회
    List<ConvLikeEntity> findByMemberIdAndConvPostIdIn(UUID memberId, List<String> convPostIds);

    boolean existsByConvPostIdAndMemberId(String convPostId, UUID memberId);
    void deleteByConvPostIdAndMemberId(String convPostId, UUID memberId);
}
