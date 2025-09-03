package kr.modusplant.framework.out.persistence.jpa.repository;

import kr.modusplant.framework.out.persistence.jpa.entity.CommLikeEntity;
import kr.modusplant.framework.out.persistence.jpa.entity.compositekey.CommPostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommLikeRepository extends JpaRepository<CommLikeEntity, CommPostLikeId> {

    List<CommLikeEntity> findByMemberId(UUID memberId);

    List<CommLikeEntity> findByMemberIdAndPostIdIn(UUID memberId, List<String> postIds);

    boolean existsByPostIdAndMemberId(String postId, UUID memberId);

    void deleteByPostIdAndMemberId(String postId, UUID memberId);
}
