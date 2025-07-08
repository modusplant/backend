package kr.modusplant.domains.communication.persistence.repository;

import kr.modusplant.domains.communication.persistence.entity.CommLikeEntity;
import kr.modusplant.domains.communication.persistence.entity.CommLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommLikeRepository extends JpaRepository<CommLikeEntity, CommLikeId> {

    List<CommLikeEntity> findByMemberId(UUID memberId);

    List<CommLikeEntity> findByMemberIdAndPostIdIn(UUID memberId, List<String> postIds);

    boolean existsByPostIdAndMemberId(String postId, UUID memberId);

    void deleteByPostIdAndMemberId(String postId, UUID memberId);
}
